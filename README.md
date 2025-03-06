# ARGENT: Energy-Aware Scheduling in Edge Computing Using Energy Valley Optimizer

## Abstract

This paper presents a novel approach for energy-aware task scheduling in heterogeneous edge computing environments. The Energy Valley Optimizer (EVO) is exploited for the optimal allocation of tasks to edge devices with limited resources. The primary objective of this algorithm is to minimize energy consumption and task completion time while achieving optimal resource utilization. EVO updates task positions in the search space using our proposed alpha, beta, and gamma reduction processes, which help avoid local optima. Our evaluations on both small- and large-scale real-world datasets indicate that our approach reduces energy consumption by an average of 14.42% (up to 20.96% in savings) and decreases task completion time by an average of 15.98% (with a peak improvement of 24.42%) compared to state-of-the-art methods. Additionally, EVO achieves a 5.28% improvement in the success ratio, demonstrating its robustness in meeting task deadlines.

---

## Overview

**ARGENT** is an energy-aware scheduling framework for edge computing that leverages the Energy Valley Optimizer (EVO) to assign tasks to resource-constrained edge devices dynamically. By incorporating innovative alpha, beta, and gamma reduction processes, ARGENT effectively navigates the search space to avoid local optima and achieve optimal resource allocation. This approach minimizes energy consumption and task completion time and improves the success ratio in meeting deadlines, making it highly suitable for heterogeneous edge computing scenarios.

---

## Key Features

- **Energy Valley Optimizer (EVO):** Employs novel reduction processes (alpha, beta, gamma) to update task positions and avoid local optima.
- **Multi-Objective Optimization:** Simultaneously minimizes energy consumption and task completion time while maximizing resource utilization.
- **Heterogeneous Scheduling:** Designed for diverse edge devices with varying resource constraints.
- **Robust Performance:** Demonstrates significant improvements over state-of-the-art methods, with up to 20.96% energy savings and 24.42% reduced task completion time.
- **Real-World Evaluation:** Validated on both small and large scale datasets, showing a 5.28% improvement in success ratio.

---

### Experimentation

- **Parameter Tuning:** Adjust parameters in the configuration file (e.g., reduction factors for alpha, beta, gamma) to explore different scheduling strategies.
- **Evaluation Metrics:** The system logs performance metrics including energy consumption, task completion time, and success ratio.
- **Visualization:** Use the provided Jupyter notebooks in the `notebooks/` folder for in-depth performance analysis and visualization.

---

## Evaluation

Our evaluations using real-world datasets demonstrate that ARGENT:
- Reduces energy consumption by an average of 14.42% (up to 20.96%).
- Decreases task completion time by an average of 15.98% (up to 24.42%).
- Improves the success ratio by 5.28%, ensuring robust performance in meeting deadlines.

---

## Citation

If you find ARGENT useful for your research, please cite our paper as follows (Still waiting for Doi):
```bibtex
@inproceedings{toghani2025argent,
  title={ARGENT: Energy-Aware Scheduling in Edge Computing Using Energy Valley Optimizer},
  author={Toghani, Mehrab and Maleki, Sareh and Younesi, Abolfazl and Safari, Sepideh and Hessabi, Shaahin},
  booktitle={Proceedings of the 2025 29th International Computer Conference, CSICC},
  year={2025},
  address={Tehran, Iran}
}
```

---

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your improvements or bug fixes. For significant changes, open an issue to discuss your ideas beforehand.

---
