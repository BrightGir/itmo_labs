import pandas as pd
import matplotlib.pyplot as plt

data = pd.read_csv('ysi.csv', delimiter=';')

data.groupby('<DATE>').boxplot(figsize=(13, 13), fontsize=15)
plt.show()