#!/usr/bin/python3

import os
import cgi
import subprocess

if "QUERY_STRING" not in os.environ:
    os.environ["QUERY_STRING"] = ""
if "REQUEST_METHOD" not in os.environ:
    os.environ["REQUEST_METHOD"] = "POST"

print("Content-type:text/html\n\n")

HTML_base = """
    <html>
        <head>
            <title>Gruppe 5 ProPra Blockteil</title>
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <link rel="stylesheet" href="style.css">
        </head>
        <body>
            <div id="header">
                <h1 style="margin: 0; font-size: 2rem;color:black">ProPra</h1>
                    <div style="display: flex; align-items: center; gap: 10px;padding-left:20px">
                        <img src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQrLDPfJQtB8R-5KC8gmwQwGroi1pNR2u7LrQ&s" alt="tulogo" style="width: 75px; height: 75px;">
                        <img src="https://cms-cdn.lmu.de/media/07-medizin/lmu_logo_1_1_format_s.png" alt="lmulogo" style="width: 75px; height: 75px;">
                    </div>
            </div>
            <div id="menu">
                <a href="home1.html">Home</a>
                <a href="swissprot_cgi.py">SwissProt</a>
                <a href="psscan_cgi.py">PsScan</a>
                <a href="acsearch_cgi.py">AC Search</a>
                <a href="genome_report.cgi.py">Genome Report</a>
                <a href="genom2aa_cgi.py">Genome 2 AAs</a>
                <a href="homstrad_cgi.py">PDB Alignment</a>

            </div>
            <div id="content">
                <p>This tool retrieves the sequence for a SwissProt AC number (e.g. P12345) from Uniprot </p>
                <p>The output is in the FASTA format.</p>
                <ul>
                    <li> Write the AC Number of the sequence you are looking for.</li>
                </ul>
                
"""

HTML_form = """
    <form name="input" action="acsearch_cgi.py" method="POST" enctype="multipart/form-data"style="display: flex; justify-content: center; align-items: center; gap: 10px;">
        <input type="text" id="acNumber" name="acNumber" placeholder = "ENTER AC NUMBER">
        <input type="submit" name="submit" value="Submit" style="padding: 5px 10px; background-color:light gray ; color: black; border: none; border-radius: 5px; cursor: pointer; transition: background-color 0.3s;">
    </form>
"""
HTML_END = "\t</div></body>\n</html>"
out = None
form = cgi.FieldStorage()
acNumber = form.getvalue("acNumber") #get the input value

if acNumber: #if there is an ac number
    try:
        out = subprocess.check_output(["python3", "/home/f/fernandezschmutz/public_html/acsearchWithString.py",
             "--ac", acNumber, "--html"],
        stderr=subprocess.PIPE)
        out = out.decode('utf-8', 'ignore')

    except subprocess.CalledProcessError as e:
        out = None
        if out is None:
            out = "no output generated in the process"

headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0'}

print(HTML_base)

if out:
    print(f"<pre>{out}</pre>")
else:
    print(HTML_form)

print(HTML_END)
