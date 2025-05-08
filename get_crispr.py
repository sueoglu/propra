#!/usr/bin/python3
import sys
import argparse


parser = argparse.ArgumentParser()
parser.add_argument("--fasta", required=True, help="Path to the FASTA file")
args = parser.parse_args()

fasta = args.fasta

def read_fasta(fasta_file):
    sequences = {} #dict of fasta headers and seq
    with open(fasta_file, "r") as file:
        current_id = None #header
        current_seq = [] #seq
        for line in file:
            line = line.strip() #remove whitespace
            if line.startswith(">"): #headers always start with >
                if current_id: #if already exists
                    sequences[current_id] = "".join(current_seq)  #join current seq into a string
                current_id = line[1:]  #extract header
                current_seq = []  #reset
            else:
                current_seq.append(line)  #if no header, it is a part of the current seq
        if current_id:
            sequences[current_id] = "".join(current_seq) #to add the very last seq

    return sequences


def find_crispr_sequences(fasta_file):
    sequences = read_fasta(fasta_file)

    for record_id, seq in sequences.items():
        for i in range(len(seq) - 22):
            if seq[i + 19:i + 21] == "GG":
                start_pos = i
                crispr_seq = seq[i-2:i + 21]
                if crispr_seq:
                    sys.stdout.write(f">{record_id}\t{start_pos-1}\n{crispr_seq}\n")

#print(read_fasta(fasta)) zum debuggen
find_crispr_sequences(fasta)
