#!/usr/bin/python3
import argparse
import sys


parser = argparse.ArgumentParser()
parser.add_argument("--genome", required=True, help="genome file")

args = parser.parse_args()

file = args.genome


def read_genome_file(input_file):
    seq = []
    with open(input_file, "r") as genome_file:
        for line in genome_file:
            if not line.startswith(">"):
                seq.append(line.strip())

    return "".join(seq)


def expected_count(substring, genome):
    genome_length = len(genome)
    total = len(substring)
    exp = (1/4**total) * (genome_length - total + 1)
    return exp


def actual_count(substring, genome):
    genome_length = len(genome)
    count_of_A = 0
    count_of_C = 0
    count_of_G = 0
    count_of_T = 0

    for nuc in genome:
        if nuc == "A":
            count_of_A += 1
        if nuc == "C":
            count_of_C += 1
        if nuc == "G":
            count_of_G += 1
        if nuc == "T":
            count_of_T += 1

    rH_adenine = (count_of_A / genome_length)
    rH_cytosine = (count_of_C / genome_length)
    rH_guanine = (count_of_G / genome_length)
    rH_thymine = (count_of_T / genome_length)

    act = 1
    for sub_nuc in substring:
        if sub_nuc == "A":
            act = act * rH_adenine
        if sub_nuc == "C":
            act = act * rH_cytosine
        if sub_nuc == "G":
            act = act * rH_guanine
        if sub_nuc == "T":
            act = act * rH_thymine

    return act * genome_length


def fold_change(actual_indices, expected_indices): #https://www.biostars.org/p/342756/
    fc = actual_indices / expected_indices
    return fc


def incidence_seq(seq, genome): #method to count incidences in the genome
    incidence = 0
    countOfNucInSeq = len(seq)

    for i in range(len(genome)):
        if genome[i:i+countOfNucInSeq] == seq:
            incidence = incidence + 1

    return incidence


def prozent(genome):
    genome_length = len(genome)
    count_of_A = 0
    count_of_C = 0
    count_of_G = 0
    count_of_T = 0

    for nuc in genome:
        if nuc == "A":
            count_of_A += 1
        if nuc == "C":
            count_of_C += 1
        if nuc == "G":
            count_of_G += 1
        if nuc == "T":
            count_of_T += 1

    rH_adenine = (count_of_A / genome_length)
    rH_cytosine = (count_of_C / genome_length)
    rH_guanine = (count_of_G / genome_length)
    rH_thymine = (count_of_T / genome_length)

    return print("A: " + str(round(rH_adenine * 100, 2)) + " " + "C: " + str(round(rH_cytosine * 100, 2)) + " " + "G: " + str(round(rH_guanine * 100, 2)) + " " + "T: " + str(round(rH_thymine * 100, 2)))

exp_CTAG = round(expected_count("CTAG", read_genome_file(file)), 0)
exp_CG = round(expected_count("CG", read_genome_file(file)), 0)
exp_AACCCTGTC = round(expected_count("AACCCTGTC", read_genome_file(file)), 0)
exp_ATG = round(expected_count("ATG", read_genome_file(file)), 0)

act_CTAG = round(actual_count("CTAG", read_genome_file(file)), 0)
act_CG = round(actual_count("CG", read_genome_file(file)), 0)
act_AACCCTGTC = round(actual_count("AACCCTGTC", read_genome_file(file)), 0)
act_ATG = round(actual_count("ATG", read_genome_file(file)), 0)

inc_CTAG = round(incidence_seq("CTAG", read_genome_file(file)), 0)
inc_CG = round(incidence_seq("CG", read_genome_file(file)), 0)
inc_AACCCTGTC = round(incidence_seq("AACCCTGTC", read_genome_file(file)), 0)
inc_ATG = round(incidence_seq("ATG", read_genome_file(file)), 0)

fc_CTAG = round(fold_change(inc_CTAG, exp_CTAG), 1)
fc_CG = round(fold_change(inc_CG, exp_CG), 1)
fc_AACCCTGTC = round(fold_change(inc_AACCCTGTC, exp_AACCCTGTC), 1)
fc_ATG = round(fold_change(inc_ATG, exp_ATG), 1)

print(prozent(read_genome_file(file)))
print("CTAG:\t" + str(exp_CTAG) + "\t" + str(act_CTAG) + "\t" + str(inc_CTAG) + "\t" + str(fc_CTAG))
print("CG:\t" + str(exp_CG) + "\t" + str(act_CG) + "\t" + str(inc_CG) + "\t" + str(fc_CG))
print("AACCCTGTC:\t" + str(exp_AACCCTGTC) + "\t" + str(act_AACCCTGTC) + "\t" + str(inc_AACCCTGTC) + "\t" + str(fc_AACCCTGTC))
print("ATG:\t" + str(exp_ATG) + "\t" + str(act_ATG) + "\t" + str(inc_ATG) + "\t" + str(fc_ATG))

