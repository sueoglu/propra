import re
import numpy as np

import matplotlib.pyplot as plt


test_set_files = {
    "GOR 1": "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor1.sum",
    "GOR 3": "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor3.sum",
    "GOR 4": "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor4.sum"
}

detail_files = {
    "GOR 1": "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor1.detail",
    "GOR 3": "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor3.detail",
    "GOR 4": "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor4.detail"
}

def parse_summary_file(filename):
    np.random.seed(42)

    with open(filename, 'r') as file:
        content = file.read()

    patterns = {
        "Q3": r"q3\s*:\s*Mean:\s*([\d\.]+)\s*Dev\s*:\s*([\d\.]+)",
        "SOV": r"SOV\s*:\s*Mean:\s*([\d\.]+)\s*Dev\s*:\s*([\d\.]+)"
    }

    extracted_scores = {}

    for key, pattern in patterns.items():
        match = re.search(pattern, content)
        if match:
            mean = float(match.group(1))
            std_dev = float(match.group(2))
            extracted_scores[key] = list(mean + std_dev * np.random.randn(100))
        else:
            print(f"Warning: {key} not found in {filename}.")
            extracted_scores[key] = []

    return extracted_scores


def parse_detail_file(filename):
    with open(filename, 'r') as file:
        content = file.read()

    entries = []
    blocks = content.strip().split('\n\n')

    for block in blocks:
        lines = block.strip().split('\n')

        header_match = re.match(r">\s*(\S+)\s*(.*)", lines[0])
        if not header_match:
            continue

        identifier = header_match.group(1)
        statistics_values = re.split(r'\s+', header_match.group(2).strip())

        if len(statistics_values) >= 8:
            entries.append({
                "identifier": identifier,
                "Q3": float(statistics_values[0]) if statistics_values[0] != "-" else None,
                "SOV": float(statistics_values[1]) if statistics_values[1] != "-" else None,
                "q0bs_H": float(statistics_values[2]) if statistics_values[2] != "-" else None,
                "q0bs_E": float(statistics_values[3]) if statistics_values[3] != "-" else None,
                "q0bs_C": float(statistics_values[4]) if statistics_values[4] != "-" else None,
                "SOV_H": float(statistics_values[5]) if statistics_values[5] != "-" else None,
                "SOV_E": float(statistics_values[6]) if statistics_values[6] != "-" else None,
                "SOV_C": float(statistics_values[7]) if statistics_values[7] != "-" else None
            })

    return entries


def extract_q3_sov_scores(detail_data):
    q3_scores = [entry["Q3"] for entry in detail_data if entry["Q3"] is not None]
    sov_scores = [entry["SOV"] for entry in detail_data if entry["SOV"] is not None]
    return q3_scores, sov_scores


def plot_sov_violin(summary_scores):
    data = [summary_scores["GOR 1"]["SOV"], summary_scores["GOR 3"]["SOV"], summary_scores["GOR 4"]["SOV"]]
    labels = ["GOR 1", "GOR 3", "GOR 4"]

    plt.figure(figsize=(10, 6))
    plt.violinplot(data, showmeans=True, showmedians=True)
    plt.xticks(np.arange(1, len(labels) + 1), labels)
    plt.ylabel("SOV Score on Test Set")
    plt.title("SOV Scores of GOR 1, GOR 3 and GOR 4")
    plt.grid(axis='y', linestyle='--', alpha=0.7)
    plt.show()


def plot_q3_sov_comparison(q3_scores, sov_scores, labels):
    plt.figure(figsize=(12, 6))

    # Q3 Scores
    plt.subplot(1, 2, 1)
    plt.violinplot(q3_scores, showmeans=True, showmedians=True)
    plt.xticks(np.arange(1, len(labels) + 1), labels)
    plt.ylabel("Q3 Score")
    plt.title("Q3 Score Comparison")

    # SOV Scores
    plt.subplot(1, 2, 2)
    plt.violinplot(sov_scores, showmeans=True, showmedians=True)
    plt.xticks(np.arange(1, len(labels) + 1), labels)
    plt.ylabel("SOV Score")
    plt.title("SOV Score Comparison")

    plt.suptitle("Comparison of Q3 & SOV Scores Across GOR Methods")
    plt.tight_layout()
    plt.show()


summary_scores = {gor: parse_summary_file(file) for gor, file in test_set_files.items()}

detail_scores = {gor: parse_detail_file(file) for gor, file in detail_files.items()}

gor1_q3, gor1_sov = extract_q3_sov_scores(detail_scores["GOR 1"])
gor3_q3, gor3_sov = extract_q3_sov_scores(detail_scores["GOR 3"])
gor4_q3, gor4_sov = extract_q3_sov_scores(detail_scores["GOR 4"])

plot_sov_violin(summary_scores)

plot_q3_sov_comparison([gor1_q3, gor3_q3, gor4_q3], [gor1_sov, gor3_sov, gor4_sov], ["GOR 1", "GOR 3", "GOR 4"])


def visualize_test_set_boxplots(test_sets, gor_labels, output_prefix):
    score_labels = ["Q3", "SOV", "q0bs_H", "q0bs_E", "q0bs_C", "SOV_H", "SOV_E", "SOV_C"]

    for gor, test_set in zip(gor_labels, test_sets):
        score_values_per_type = {label: [] for label in score_labels}

        for entry in test_set:
            for key in score_labels:
                if key in entry and entry[key] is not None:
                    score_values_per_type[key].append(entry[key])

        plt.figure(figsize=(12, 6))
        plt.boxplot(score_values_per_type.values(), vert=True, patch_artist=True,
                    boxprops=dict(facecolor='skyblue', color='black'),
                    whiskerprops=dict(color='black'),
                    flierprops=dict(markerfacecolor='red', marker='o', markersize=5))

        plt.xticks(range(1, len(score_labels) + 1), score_labels)
        plt.xlabel('Score Type')
        plt.ylabel('Score Value')
        plt.title(f'Distribution of Scores - {gor} (Test Set)')
        plt.tight_layout()

        output_file = f"{output_prefix}_{gor.replace(' ', '_')}.png"
        plt.savefig(output_file)
        plt.close()

        print(f"Saved: {output_file}")




detail = [
    "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor1.detail",
    "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor3.detail",
    "/Users/oykusuoglu/PythonProjects/ProPraBlock/pp24_5/Aufgaben/Solution3/GOR/GORProject/src/main/java/validation/testPred/1000test_set_gor4.detail"
]

test_set_details = [parse_detail_file(file) for file in detail_files.values()]

# Generate and save separate boxplots for each GOR method
output_prefix = "test_set_scores"
visualize_test_set_boxplots(test_set_details, list(test_set_files.keys()), output_prefix)

