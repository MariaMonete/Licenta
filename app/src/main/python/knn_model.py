import pandas as pd
from sklearn.neighbors import NearestNeighbors
import numpy as np
import json
import joblib

# Încarcă datele din CSV
data = pd.read_csv('parfumes.csv')

# Extrage notele parfumurilor și le transformă într-o reprezentare numerica
def extract_notes(notes_str):
    notes_list = json.loads(notes_str)
    return notes_list

data['notes_list'] = data['notes'].apply(extract_notes)

# Crează un set de note unic
unique_notes = set(note for notes in data['notes_list'] for note in notes)

# Mapare note la indici
note_index = {note: idx for idx, note in enumerate(unique_notes)}

# Transformă notele parfumurilor în vectori binari
def notes_to_vector(notes, note_index):
    vector = np.zeros(len(note_index))
    for note in notes:
        vector[note_index[note]] = 1
    return vector

data['notes_vector'] = data['notes_list'].apply(lambda x: notes_to_vector(x, note_index))

# Crează matricea de caracteristici
X = np.stack(data['notes_vector'].values)

# Antrenează modelul KNN
knn = NearestNeighbors(n_neighbors=5, metric='cosine')
knn.fit(X)

# Salvează modelul antrenat
joblib.dump(knn, 'knn_model.pkl')
joblib.dump(note_index, 'note_index.pkl')
