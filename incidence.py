#!/usr/bin/python3

import argparse
import sys


parser = argparse.ArgumentParser()
parser.add_argument("--sequence", nargs="+",required=True, help= "sequences whose frequency is searched")
parser.add_argument("--genome", required=True, help="genome file")

args = parser.parse_args()

sequenceList = args.sequence

#print("List generated") #debug
#print(sequenceList) #debug
def incidence_seq(seq, genome): #method to count incidences in the genome
    incidence = 0
    countOfNucInSeq = len(seq)

    for i in range(len(genome)): #count every nuc in genome
        if genome[i:i+countOfNucInSeq] == seq: #wenn ein Substring auf dem genome (von i-te Stelle bis i + Länge des Substrings) zu finden ist, wird die Anzahl um 1 erhöht
            incidence = incidence + 1

    return incidence #count of incidences of given seq

try:
    with open(args.genome, "r") as genomeFile: #read input file consiting of genome (if fasta has only one organism-sequenz)
        lines = genomeFile.readlines() #list
        header = lines[0].strip() #weil die erste zeile immer die Header ist
        sequence = "" #leere String, wo die Sequenz gespeichert wird
        for line in lines[1:]: #beginnt von der 2. Zeile zu lesen
            line = line.strip().replace("\n", "") #um zu vermeiden unnötige newline characters im sequence String
            sequence += line

        searchedGenome = sequence #das fertige Sequenz

    sequence_list_length = len(sequenceList)

    for i in range(sequence_list_length):
        sequence = sequenceList[i] #i-te Subsequenz in der input list
        #sys.stdout.write(str(sequence) + ": " + str(incidence_seq(sequence, searchedGenome)))
        print(str(sequence) + ": " + str(incidence_seq(sequence, searchedGenome)))

except Exception as e:
    print(f"unexpected error: {str(e)}")
    sys.exit(1)


