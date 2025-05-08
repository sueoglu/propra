#!/usr/bin/python3

import sys
import argparse
import requests
import math
import matplotlib.pyplot as plt
import seaborn as sns

parser = argparse.ArgumentParser()
parser.add_argument('--id', help="PDB ID", required=True)
parser.add_argument('--distance', help="contact distance", required=True)
parser.add_argument('--type', help="atom type", required=True)
parser.add_argument('--length', help="sequence distance for local contacts", required=True)
parser.add_argument('--contactmatrix', help="contact matrix", action="store_true")

args = parser.parse_args()

PDBID = args.id
distance = float(args.distance)
a_type = args.type
seq_dist = float(args.length)


def read_pdb(pdb_file):
    model1 = []  #string list
    in_the_model = False
    found_model = False

    for line in pdb_file.splitlines():
        if line.startswith('MODEL'):
            found_model = True
            model_num = int(line.strip().split()[-1])
            if model_num == 1:
                in_the_model = True
            else: 
                break

        if line.startswith('ATOM'):
            if not found_model:
                model1.append(line.strip())
            elif in_the_model:
                model1.append(line.strip())

        if line.startswith('ENDMDL'):
            break

    return model1


def find_ss(pdb_file):
    secondary_structure = {}

    for line in pdb_file.splitlines():
        line = line.strip()

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


def create_dictionaries(pdb_data, pdb_file, given_dist, given_seq_dist, compute_contact_matrix=False):
    contacts_table = {
        "chain": [],
        "pos": [],
        "serial": [],
        "aa": [],
        "ss": [],
        "global": [],
        "local": []
    }
    x_y_z_Cor = {
        "x": [],
        "y": [],
        "z": []
    }

    Aminoacids = {
        "ALA": "A", "ARG": "R", "ASP": "D", "ASN": "N",
        "CYS": "C", "GLU": "E", "GLN": "Q", "GLY": "G",
        "HIS": "H", "ILE": "I", "LEU": "L", "LYS": "K",
        "MET": "M", "PHE": "F", "PRO": "P", "SER": "S",
        "THR": "T", "TRP": "W", "TYR": "Y", "VAL": "V"
    }

    ss_dict = find_ss(pdb_file)

    for atom in pdb_data:
        atom = atom.strip()
        serial = int(atom[6:11])
        atom_type = atom[12:16] #.strip()
        residue_name = atom[17:20]
        chain = atom[21]
        position = int(atom[22:26])
        x_cor = float(atom[30:38])
        y_cor = float(atom[38:46])
        z_cor = float(atom[46:54])

        #if atom_type.split(" ")[1] == "CA":
        if atom_type.strip() == args.type:
            contacts_table["chain"].append(chain)
            contacts_table["pos"].append(position)
            contacts_table["serial"].append(serial)
            contacts_table["aa"].append(Aminoacids[residue_name])

            x_y_z_Cor["x"].append(x_cor)
            x_y_z_Cor["y"].append(y_cor)
            x_y_z_Cor["z"].append(z_cor)

    n = len(contacts_table["pos"])
    contact_matrix = [[0] * n for a in range(n)] if compute_contact_matrix else None  # Only create if requested
    distance_matrix = [[0] * n for a in range(n)] if compute_contact_matrix else None

    for i in range(n):
        act_atom_x = float((x_y_z_Cor["x"][i]))
        act_atom_y = float(x_y_z_Cor["y"][i])
        act_atom_z = float(x_y_z_Cor["z"][i])
        act_atom_pos = int(contacts_table["pos"][i])

        local_cont_act = 0
        global_cont_act = 0

        for j in range(n):
            if i != j:
                other_atom_x = float(x_y_z_Cor["x"][j])
                other_atom_y = float(x_y_z_Cor["y"][j])
                other_atom_z = float(x_y_z_Cor["z"][j])

                other_atom_pos = int(contacts_table["pos"][j])

                diff_of_positions = abs(act_atom_pos - other_atom_pos)  #Sequenzdistanz
                euklidian_dist = math.sqrt((act_atom_x - other_atom_x) ** 2 +
                                            (act_atom_y - other_atom_y) ** 2 +
                                            (act_atom_z - other_atom_z) ** 2)  #Kontaktdistanz

                distance_matrix[i][j] = float(euklidian_dist)
                if euklidian_dist < given_dist:
                    if compute_contact_matrix:
                        contact_matrix[i][j] = 1
                    if diff_of_positions < given_seq_dist:
                        local_cont_act += 1
                    elif diff_of_positions >= given_seq_dist:
                        global_cont_act += 1

        contacts_table["global"].append(global_cont_act)
        contacts_table["local"].append(local_cont_act)

        key = (contacts_table["chain"][i], act_atom_pos)
        ss_value = ss_dict.get(key, "C")
        contacts_table["ss"].append(ss_value)

    final_table = list(zip(
        contacts_table["chain"],
        contacts_table["pos"],
        contacts_table["serial"],
        contacts_table["aa"],
        contacts_table["ss"],
        contacts_table["global"],
        contacts_table["local"]
    ))

    return final_table, contact_matrix, distance_matrix


def save_matrix_file(matrix, filename=f"contact_matrix_{PDBID}.csv"):
    with open(filename, "w") as file:
        for row in matrix:
            file.write(",".join(map(str, row)) + "\n")


def create_heatmap(distance_matrix, filename=f"heatmap_{PDBID}.png"):
    plt.figure(figsize=(10,8))
   #plt.imshow(distance_matrix, cmap="coolwarm", interpolation="sinc")
    sns.heatmap(distance_matrix, annot=False, fmt=".2f", cmap="inferno", rasterized=True, linewidths=0, square=True)
    plt.title("Distance Heatmap")
    plt.xlabel("Residue Index i")
    plt.ylabel("Residue Index j")
    plt.savefig(filename)
    plt.show()


url = f"https://files.rcsb.org/download/{PDBID}.pdb"
response = requests.get(url)
if response.status_code == 200:
    pdb_content = response.text
else:
    sys.stderr.write(f"Error: no data found for PDB ID {PDBID}\n")
    sys.exit(0)

parsed_pdb = read_pdb(pdb_content)
structured_table, contact_matrix, distance_matrix = create_dictionaries(parsed_pdb, pdb_content, distance, seq_dist, args.contactmatrix)

# To save the sscc file
sscc_filename = f"{PDBID}.sscc"

with open(sscc_filename, "w", newline='') as sscc_file:
    sscc_file.write("chain\tpos\tserial\taa\tss\tglobal\tlocal\n")
    for row in structured_table:
       sscc_file.write("\t".join(str(x).strip() for x in row) + "\n")

if args.contactmatrix:
    save_matrix_file(contact_matrix, f"contact_matrix_{PDBID}.csv")
    save_matrix_file(distance_matrix, f"distance_matrix{PDBID}.csv")
    #plot_contact_matrix(contact_matrix, "contact_heatmap.png")
    create_heatmap(distance_matrix, f"distance_heatmap{PDBID}.png")

#To write to stdout
"""
sys.stdout.write("chain\tpos\tserial\taa\tss\tglobal\tlocal\n")
for row in structured_table:
   sys.stdout.write("\t".join(str(x).strip() for x in row) + "\n")

"""