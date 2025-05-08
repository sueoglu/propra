

import argparse
import os

def exptype(Data,outputdir):
    Counts={}   #dictionary to store Data_Types
    for i in Data:  #search through each row in the given data file
        Datatype=i['Data_Type']
        if Datatype not in Counts:  #if the data type is not already in the dict then it will get the key as Data_Type and its count (value) is 1
            Counts[Datatype]=1
        else:
            Counts[Datatype]+=1  #if it exists already: add 1 to the count of that data type
    with open(os.path.join(outputdir,'exptypes.tsv'),'w') as out:  #construct a file path
        for key,val in Counts.items():  #key: data type, value: count of the data type
            out.write(key+'\t'+str(val)+'\n')  #dataypes are writen in a column and their counts written in the second column accordingly to output file that is the "outputdir"

def findchiprna(Data,outputdir):
    chipseqdic={}  #store cell types and their dcc acession in ChIP seq ex
    rnaseqdic={}
    for i in Data:
        Cell_line=i['Cell_Type']  #cell type of the row as key

        if i["Data_Type"]=="ChIP-seq" and (i["Experimental_Factors"].split(" ")[0] == "Antibody=H3K27me3"):  #exp factor column of the cell type start with Antibody=H3K27me3
            if Cell_line not in chipseqdic:
                chipseqdic[Cell_line] =[]   #Initialize list if the cell line is not already in the dictionary
            if i["DCC_Accession"].strip():
                 chipseqdic[Cell_line].append(i["DCC_Accession"])  #store dcc accession if not empty

        if i["Data_Type"]=="RNA-seq":  #same thing as above but for RNA-seq
            if Cell_line not in rnaseqdic:
                rnaseqdic[Cell_line]=[]
            if i["DCC_Accession"].strip():
                rnaseqdic[Cell_line].append(i["DCC_Accession"])

    with open(os.path.join(outputdir,'chip_rna_seq.tsv'),'w') as out:
        out.write("cell line\tRNAseq Accession\tChIPseq Accession\n")  #header
        for cell_line in chipseqdic.keys() & rnaseqdic.keys():  # cell types appear in both ChIP seq and rna seq
            chip_dcc = sorted(chipseqdic[cell_line])  #list of dcc num
            rna_dcc = sorted(rnaseqdic[cell_line])
            chip_dcc.sort()
            rna_dcc.sort()
            out.write(cell_line+'\t'+ ",".join(rna_dcc) +'\t' + ",".join(chip_dcc) + '\n')  #write as tsv

def chipseq(Data,outputdir):
    Antibodies ={}
    Cell_types = {} #dict of cel types and number of unique antibodies
    for i in Data:
        cell_line=i['Cell_Type'] #current cell type
        if cell_line not in Cell_types: #if not already in dict new cell type with empty set
            Cell_types[cell_line] = set()

        if i['Data_Type'] == "ChIP-seq": #rows with only ChIp seq
            expfactor = i['Experimental_Factors'] #where the antibody info is
            if "Antibody=" in expfactor:
                antibody = expfactor.split("Antibody=")[1].split()[0] #take the first string after antibody
                Cell_types[cell_line].add(antibody) #add extracted antibody to the set


        with open(os.path.join(outputdir, 'antibodies.tsv'), 'w') as out:
              for key, val in Cell_types.items():
                out.write(key + '\t' + str(len(val)) + '\n') #write cell types along with number of unique antibody (tsv)




        """if i['Data_Type'] == "ChIP-seq":
            cell_line=i['Cell_Type']
            if cell_line not in Antibodies:
                 Antibodies[cell_line] = set()

        


            expfactor = i['Experimental_Factors']
            if "Antibody=" in expfactor:
                antibody=expfactor.split("Antibody=")[1].split()[0]
                Antibodies[cell_line].add(antibody)
                """


    #with open(os.path.join(outputdir,'antibodies.tsv'),'w') as out:
     #   for key,val in Antibodies.items():
      #      out.write(key+'\t'+str(len(val))+'\n')

def readcsv(inputfile):
  with open(inputfile,"r") as csvfile:
      lines= csvfile.readlines()  #store lines from the file as string list
  Headers = lines[0].strip().split(",")  #the list of headers of each "column" (since input file is a csv it is comma seperated so we split it at each comma)
  Data= [dict(zip(Headers,line.strip().split(","))) for line in lines[1:]]  #each row is iterated over (header not included), then headers are matched with their values and then dict
  return Data

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("-i","--input",help="input file",required=True)
    parser.add_argument("-o","--output",help="output directory",required=True)
    args = parser.parse_args()
    Data=readcsv(args.input)
    exptype(Data,args.output)
    chipseq(Data,args.output)
    findchiprna(Data,args.output)


if __name__=="__main__":
    main()
