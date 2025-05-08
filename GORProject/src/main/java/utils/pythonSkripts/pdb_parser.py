#!/usr/bin/python3

import sys
import argparse
import requests
import os


parser = argparse.ArgumentParser()
parser.add_argument('--ids', nargs='+', help="PDB ID", required=True)
parser.add_argument('--output', help="Seclib file", required=True)

args = parser.parse_args()

pdb_id = args.ids



def get_pdb(pdbID, outputFile, fasta=False):
    if fasta:
        url =  f"https://www.rcsb.org/fasta/entry/{pdbID}"
    else:
        url = f"https://files.rcsb.org/download/{pdbID}.pdb"

    response = requests.get(url)
    if response.status_code == 200:
        pdb_file = response.text
    else:
        sys.stderr.write(f"Error: no data found for PDB ID {pdbID}\n")
        sys.exit(0)

    if outputFile == "-":
        print(pdb_file)
    else:
        with open(outputFile, "w") as out:
            out.write(pdb_file)

def parse_pdb(pdb_file):
    chain_and_seq ={}
    Aminoacids = {
        "ALA": "A", "ARG": "R", "ASP": "D", "ASN": "N",
        "CYS": "C", "GLU": "E", "GLN": "Q", "GLY": "G",
        "HIS": "H", "ILE": "I", "LEU": "L", "LYS": "K",
        "MET": "M", "PHE": "F", "PRO": "P", "SER": "S",
        "THR": "T", "TRP": "W", "TYR": "Y", "VAL": "V"
    }

    with open(pdb_file, "r") as pdb:
        for line in pdb:
            if line.startswith("SEQRES"):
                chain = line[11].strip()
                if chain not in chain_and_seq:
                    chain_and_seq[chain] = []

                residue_start_pos = 19
                residue_end_pos = 70
                aaList = line[residue_start_pos:residue_end_pos].split()

                for aa in aaList:
                    chain_and_seq[chain].append(Aminoacids[aa])

    return chain_and_seq

def create_seclib(pdb_ids, pdb_dir="pdb_files", output_file="output.seclib"):
    os.makedirs(pdb_dir, exist_ok=True)

    with open(output_file, "w") as out_f:
        for pdb_id in pdb_ids:
            pdb_path = os.path.join(pdb_dir, f"{pdb_id}.pdb")

            if not os.path.exists(pdb_path):
                get_pdb(pdb_id, pdb_path)

            sequences = parse_pdb(pdb_path)

            for chain, sequence in sequences.items():
                out_f.write(f">{pdb_id}_{chain}\n")
                out_f.write("".join(sequence) + "\n")


create_seclib(args.ids, pdb_dir="pdb_files", output_file="output.seclib")




