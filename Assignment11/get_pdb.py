import sys
import argparse
import requests

parser = argparse.ArgumentParser()
parser.add_argument('--id', required=True, help='Input file')
parser.add_argument('--output', required=True, help='Output file path')
parser.add_argument('--fasta', required=False, help='if output is in fasta format')

args = parser.parse_args()

pdbID = args.id
output_file = args.output
fasta = args.fasta

if fasta:
    url = f"https://www.rcsb.org/fasta/entry/{pdbID}"
else:
    url = f"https://files.rcsb.org/download/{pdbID}.pdb"

response = requests.get(url)
if response.status_code == 200:
    pdb_file = response.text
else:
    sys.stderr.write(f"Error: no data found for PDB ID {pdbID}\n")
    sys.exit(0)

if output_file == "-":
    print(pdb_file)
else:
    with open(output_file, "w") as out:
        out.write(pdb_file)



