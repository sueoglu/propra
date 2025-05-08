#!/usr/bin/python3

import sys
import argparse
import requests

parser = argparse.ArgumentParser()
parser.add_argument('--ac', required=True, help="SwissProt AC Number")
args = parser.parse_args()

try:
    with open(args.ac, "r") as acFile:
        acNumber = acFile.read().strip()

    uniprot = f"https://rest.uniprot.org/uniprotkb/{acNumber}.fasta"

    outputWebsite = requests.get(uniprot)
    outputWebsite.raise_for_status()

    fasta = outputWebsite.text.strip()
    fasta_line = fasta.split("\n")

    seq = "".join(fasta_line[1:])
    sys.stdout.write(seq)

except Exception as e:
    print(f"unexpected error: {str(e)}")
    sys.exit(1)