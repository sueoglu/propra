#!/usr/bin/python3

import sys
import argparse
import requests


parser = argparse.ArgumentParser()
parser.add_argument('--ac', required=True, help="SwissProt AC Number")
parser.add_argument("--html", required= False ,action="store_true")
args = parser.parse_args()

acNumber = args.ac

uniprot = f"https://rest.uniprot.org/uniprotkb/{acNumber}.fasta"

try:
    outputWebsite = requests.get(uniprot)
    outputWebsite.raise_for_status()

    fasta = outputWebsite.text.strip()  #remove whitespaces from the output got from the UniProt
    #fasta_line = fasta.split("\n")

    #seq = "".join(fasta_line[1:]) if we only want the sequence
    if args.html: #for the CGI Web interface
        print(f"<pre>{fasta}</pre>")
    else:
        sys.stdout.write(fasta) #for submission server

except requests.exceptions.HTTPError as err:
    sys.stderr.write(err.__str__())
    sys.exit(0)

except Exception as e:
    print(f"unexpected error: {str(e)}")
    sys.exit(0)