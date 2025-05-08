#!/usr/bin/python3

import argparse
import random


import requests
import sys
import os
import mysql.connector
""""
parser = argparse.ArgumentParser()
parser.add_argument('--output', help="Seclib file", required=True)
parser.add_argument('--count',type =int, help="count of pdbIds", required=False)

args = parser.parse_args()
"""


script_dir = os.path.dirname(os.path.abspath(__file__))
tmp_dir = os.path.join(script_dir, "tmp_PDB")

os.makedirs(tmp_dir, exist_ok=True)

bioprakt_db = mysql.connector.connect(
    host="localhost",
    port=3307,
    user="bioprakt5",
    password="$1$Q7JqBgkq$RzHNNur/mPHrVC1T.6lvI/",
    database="bioprakt5")

cursor = bioprakt_db.cursor()

pdb_id_query = "SELECT DISTINCT PDB_ID FROM Sequence LIMIT 1000"
cursor.execute(pdb_id_query)

print("pdb_id_query")

pdb_ids_as_tuples = cursor.fetchall()

pdb_id_list = []
for tupel in pdb_ids_as_tuples:
    pdb = tupel[0]
    pdb_id = pdb[0:4]
    pdb_id_list.append(pdb_id)


print(pdb_id_list)


def get_pdb(pdbID, fasta=False):
    if fasta:
        url =  f"https://www.rcsb.org/fasta/entry/{pdbID}"
    else:
        url = f"https://files.rcsb.org/download/{pdbID}.pdb"

    response = requests.get(url)
    if response.status_code == 200:
        pdb_file = response.text
    else:
        sys.stderr.write(f"Error: no data found for PDB ID {pdbID}\n")
        return None

    outputFile = os.path.join(tmp_dir, f"{pdbID}.pdb")

    if outputFile == "-":
        print(pdb_file)
    else:
        with open(outputFile, "w") as out:
            out.write(pdb_file)

    return outputFile


def parse_pdb(pdb_file):
    chain_and_seq ={}
    Aminoacids = {
        "ALA": "A", "ARG": "R", "ASP": "D", "ASN": "N",
        "CYS": "C", "GLU": "E", "GLN": "Q", "GLY": "G",
        "HIS": "H", "ILE": "I", "LEU": "L", "LYS": "K",
        "MET": "M", "PHE": "F", "PRO": "P", "SER": "S",
        "THR": "T", "TRP": "W", "TYR": "Y", "VAL": "V"
    }

    if not os.path.exists(pdb_file) or os.path.getsize(pdb_file) == 0:
        print("PDB file not found")
        return {}

    found_seqres = False
    with open(pdb_file, "r") as pdb:
        for line in pdb:
            if line.startswith("SEQRES"):
                found_seqres = True
                chain = line[11].strip()
                if chain == 'A':
                    if chain not in chain_and_seq:
                        chain_and_seq[chain] = []

                    residue_start_pos = 19
                    residue_end_pos = 70
                    aaList = line[residue_start_pos:residue_end_pos].split()

                    for aa in aaList:
                        if aa in Aminoacids:
                            chain_and_seq[chain].append(Aminoacids[aa])

    if not found_seqres:
        print("No seqres line found")
        return{}
    return chain_and_seq


def find_ss(pdb_file):
    secondary_structure = {}

    with open(pdb_file, "r") as pdb:
        for line in pdb:
            if line.startswith("HELIX"):
                chain_id = line[19]  # Chain identifier
                start_residue = int(line[21:25].strip())  # Start position
                end_residue = int(line[33:37].strip())  # End position
                structure_type = "H"

                for pos in range(start_residue, end_residue + 1):
                    secondary_structure[(chain_id, pos)] = structure_type
            elif line.startswith("SHEET"):
                chain_id = line[21]  # Chain identifier
                start_residue = int(line[22:26].strip())  # Start position
                end_residue = int(line[33:37].strip())  # End position
                structure_type = "E"

                for pos in range(start_residue, end_residue + 1):
                    secondary_structure[(chain_id, pos)] = structure_type


    return secondary_structure




def create_seclib(pdbIDs, output):
    valid_IDS = []

    with open(output, "w") as out_f:
        for pdb_id in pdbIDs:
            pdb_path = get_pdb(pdb_id)
            if not pdb_path:
                continue

            sequences = parse_pdb(pdb_path)
            if not sequences or all(len(seq) == 0 for seq in sequences.values()):
                print(f"No sequences found, remove {pdb_id}")
                os.remove(pdb_path)
                continue

            valid_IDS.append(pdb_id)
            ss_map = find_ss(pdb_path)

            for chain, sequence in sequences.items():
                if len(sequence) == 0:
                    print(f" Skipping empty chain {pdb_id}_{chain}")
                    continue
                out_f.write(f">{pdb_id}_{chain}\n")

                ss_sequence = "".join(ss_map.get((chain, i + 1), "C") for i in range(len(sequence)))

                out_f.write(f"AS {''.join(sequence)}\n")
                out_f.write(f"SS {ss_sequence}\n")


create_seclib(pdb_id_list, "test1000biopraktA.seclib")

