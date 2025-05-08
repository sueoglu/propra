""" import os
import subprocess

gor_num = input("GOR Number: ") or "1"

# config
jar_file = "C:/Users/sabri/Downloads/validateGor.jar"
input_folder = f'C:/Users/sabri/OneDrive/Dokumente/CV_GOR{gor_num}/'
output_details_folder = f'C:/Users/sabri/OneDrive/Dokumente/CV_GOR{gor_num}/details/'
output_summary_folder = f'C:/Users/sabri/OneDrive/Dokumente/CV_GOR{gor_num}/summary/'

# Verzeichnisse erstellen, falls sie nicht existieren
os.makedirs(output_details_folder, exist_ok=True)
os.makedirs(output_summary_folder, exist_ok=True)

files = [f for f in os.listdir(input_folder) if os.path.isfile(os.path.join(input_folder, f))]


for file in files:
    path = os.path.join(input_folder, file)
    print("processing: " + file)

    try:
        cmd = ["java", "-jar", jar_file, 
               "-p", path, 
               "-r", "C:/Users/sabri/Downloads/CB513DSSP.db",
               "-d", output_details_folder,
               "-s", output_summary_folder,
               "-f", "txt"]


        result = subprocess.run(cmd, capture_output=True, text=True)

    
        if result.returncode != 0:
            print(f"Fehler beim Verarbeiten von {file}: Rückgabewert {result.returncode}")
            print(f"Fehlerausgabe: {result.stderr}")
        else:
            print(f"Erfolgreich verarbeitet: {file}")
            print(f"Ausgabe: {result.stdout}")
    
    except Exception as e:
        print(f"Fehler beim Ausführen des Befehls für {file}: {str(e)}") """


import os
import subprocess

# config
windowSize = list(map(str, range(7, 24)))
pseudo_counts = ['0.000000001', '0.0001', '0.01', '1', '2', '3']
gor_versions = ["gor1", "gor3", "gor4"]


output_folder = "C:/Users/sabri/OneDrive/Dokumente/ParameterTuning"
cv_jar = "C:/Users/sabri/OneDrive/Dokumente/pp24_5/src/artifacts/crossVali_jar9/crossVali.jar"
vali_jar = "C:/Users/sabri/Downloads/validateGor.jar"
seclib = "C:/Users/sabri/Downloads/CB513DSSP.db"
maf_folder = "C:/Users/sabri/OneDrive/Dokumente/CB513MultipleAlignments"

# 1. Test Window Sizes
for ws in windowSize:
    for gor in gor_versions:

        folder = f'{output_folder}/window_{ws}/{gor}/'
        folder_gor5 = f'{output_folder}/window_{ws}/{gor}/gor5/'
        
        os.makedirs(folder, exist_ok=True)
        os.makedirs(folder_gor5, exist_ok=True)

        cmd_cv = ["java", "-jar", cv_jar,
                  "--seclib", seclib,
                  "--gor", gor,
                  "--output", folder,
                  "--fold", '5',
                  "--postpr",
                  "--window", ws,
            ]
        
        cmd_cv_g5 = ["java", "-jar", cv_jar,
                     "--seclib", seclib,
                     "--gor", gor,
                     "--output", folder_gor5,
                     "--fold", '5',
                     "--postpr",
                     "--window", ws,
                     "--maf", maf_folder
                    ]
        
        cv_result = subprocess.run(cmd_cv, capture_output=True, text=True)
        cv_result_g5 = subprocess.run(cmd_cv_g5, capture_output=True, text=True)
        
        files = [f for f in os.listdir(folder) if os.path.isfile(os.path.join(folder, f))]
        files_gor5 = [f for f in os.listdir(folder_gor5) if os.path.isfile(os.path.join(folder_gor5, f))]
        
        sum_folder = f'{folder}summary/'
        detail_folder = f'{folder}details/'
        sum_folder_gor5 = f'{folder_gor5}summary/'
        detail_folder_gor5 = f'{folder_gor5}/details/'
        
        os.makedirs(detail_folder, exist_ok=True)
        os.makedirs(sum_folder, exist_ok=True)
        os.makedirs(detail_folder_gor5, exist_ok=True)
        os.makedirs(sum_folder_gor5, exist_ok=True)
    
        for file in files:
            path = os.path.join(folder, file)
            
            cmd_vali = ["java", "-jar", vali_jar, 
                        "-p", path, 
                        "-r", "C:/Users/sabri/Downloads/CB513DSSP.db",
                        "-d", detail_folder,
                        "-s", sum_folder,
                        "-f", "txt"]
            
            subprocess.run(cmd_vali)   
            
            print(f'Processed {file}, window size={ws}, gor={gor}')
            
        for file in files_gor5:    
            path_gor5 = os.path.join(folder_gor5, file)
            
            cmd_vali_gor5 = ["java", "-jar", vali_jar, 
                            "-p", path_gor5, 
                            "-r", "C:/Users/sabri/Downloads/CB513DSSP.db",
                            "-d", detail_folder,
                            "-s", sum_folder,
                            "-f", "txt"]

            subprocess.run(cmd_vali_gor5)   
            
            print(f'Processed {file}, window size={ws}, gor={gor}, gor5')
    
        
# 2. Test Pseudo Counts
for pc in pseudo_counts:
    for gor in gor_versions:

        folder = f'{output_folder}/pc_{pc}/{gor}/'
        folder_gor5 = f'{output_folder}/pc_{pc}/{gor}/gor5/'
        
        os.makedirs(folder, exist_ok=True)
        os.makedirs(folder_gor5, exist_ok=True)

        cmd_cv = ["java", "-jar", cv_jar,
                  "--seclib", seclib,
                  "--gor", gor,
                  "--output", folder,
                  "--fold", '5',
                  "--postpr",
                  "--pseudoc", pc,
            ]
        
        cmd_cv_g5 = ["java", "-jar", cv_jar,
                     "--seclib", seclib,
                     "--gor", gor,
                     "--output", folder_gor5,
                     "--fold", '5',
                     "--postpr",
                     "--pseudoc", pc,
                     "--maf", maf_folder
                    ]
        
        cv_result = subprocess.run(cmd_cv, capture_output=True, text=True)
        cv_result_g5 = subprocess.run(cmd_cv_g5, capture_output=True, text=True)
        
        files = [f for f in os.listdir(folder) if os.path.isfile(os.path.join(folder, f))]
        files_gor5 = [f for f in os.listdir(folder_gor5) if os.path.isfile(os.path.join(folder_gor5, f))]
        
        sum_folder = f'{folder}/summary/'
        detail_folder = f'{folder}/details/'
        sum_folder_gor5 = f'{folder_gor5}/summary/'
        detail_folder_gor5 = f'{folder_gor5}/details/'
        
        os.makedirs(detail_folder, exist_ok=True)
        os.makedirs(sum_folder, exist_ok=True)
        os.makedirs(detail_folder_gor5, exist_ok=True)
        os.makedirs(sum_folder_gor5, exist_ok=True)
    
        for file in folder:
            path = os.path.join(folder, file)
        
            cmd_vali = ["java", "-jar", vali_jar, 
                        "-p", path, 
                        "-r", "C:/Users/sabri/Downloads/CB513DSSP.db",
                        "-d", detail_folder,
                        "-s", sum_folder,
                        "-f", "txt"]
            
            subprocess.run(cmd_vali)   
            
            print(f'Processed {file}, window size={ws}, gor={gor}')
            
        for file in folder_gor5:    
            path_gor5 = os.path.join(folder_gor5, file)
            
            cmd_vali_gor5 = ["java", "-jar", vali_jar, 
                            "-p", path_gor5, 
                            "-r", "C:/Users/sabri/Downloads/CB513DSSP.db",
                            "-d", detail_folder,
                            "-s", sum_folder,
                            "-f", "txt"]

            subprocess.run(cmd_vali_gor5)   
            
            print(f'Processed {file}, window size={ws}, gor={gor}, gor5')
    

# 3. Test Postprocessing
for gor in gor_versions:
    
    folder = f'{output_folder}/postr/{gor}/'
    folder_gor5 = f'{output_folder}/postpr/{gor}/gor5/'
    
    os.makedirs(folder, exist_ok=True)
    os.makedirs(folder_gor5, exist_ok=True)

    cmd_cv = ["java", "-jar", cv_jar,
              "--seclib", seclib,
                "--gor", gor,
                "--output", folder,
                "--fold", '5',
                "--postpr",
        ]
    
    cmd_cv_g5 = ["java", "-jar", cv_jar,
                 "--seclib", seclib,
                    "--gor", gor,
                    "--output", folder_gor5,
                    "--fold", '5',
                    "--postpr",
                    "--maf", maf_folder
                ]
    
    cv_result = subprocess.run(cmd_cv, capture_output=True, text=True)
    cv_result_g5 = subprocess.run(cmd_cv_g5, capture_output=True, text=True)
    
    files = [f for f in os.listdir(folder) if os.path.isfile(os.path.join(folder, f))]
    files_gor5 = [f for f in os.listdir(folder_gor5) if os.path.isfile(os.path.join(folder_gor5, f))]
    
    sum_folder = f'{folder}/summary/'
    detail_folder = f'{folder}/details/'
    sum_folder_gor5 = f'{folder_gor5}/summary/'
    detail_folder_gor5 = f'{folder_gor5}/details/'
    
    os.makedirs(detail_folder, exist_ok=True)
    os.makedirs(sum_folder, exist_ok=True)
    os.makedirs(detail_folder_gor5, exist_ok=True)
    os.makedirs(sum_folder_gor5, exist_ok=True)

    for file in folder:
        path = os.path.join(folder, file)
    
        cmd_vali = ["java", "-jar", vali_jar, 
                    "-p", path, 
                    "-r", "C:/Users/sabri/Downloads/CB513DSSP.db",
                    "-d", detail_folder,
                    "-s", sum_folder,
                    "-f", "txt"]
        
        subprocess.run(cmd_vali)   
        
        print(f'Processed {file}, window size={ws}, gor={gor}')
        
    for file in folder_gor5:    
        path_gor5 = os.path.join(folder_gor5, file)
        
        cmd_vali_gor5 = ["java", "-jar", vali_jar, 
                        "-p", path_gor5, 
                        "-r", "C:/Users/sabri/Downloads/CB513DSSP.db",
                        "-d", detail_folder,
                        "-s", sum_folder,
                        "-f", "txt"]

        subprocess.run(cmd_vali_gor5)   
        
        print(f'Processed {file}, window size={ws}, gor={gor}, gor5')
    