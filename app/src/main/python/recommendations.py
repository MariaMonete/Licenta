import joblib
import numpy as np
import os

# Funcție pentru a încărca un fișier din același director cu scriptul
def load_asset(filename):
    asset_path = os.path.join(os.path.dirname(__file__), filename)
    with open(asset_path, 'rb') as f:
        return joblib.load(f)

print("Loading KNN model and note index")
# Încarcă modelul KNN și maparea notelor din directorul scriptului
knn = load_asset('knn_model.pkl')
note_index = load_asset('note_index.pkl')
print("Loaded KNN model and note index")

def notes_to_vector(notes, note_index):
    vector = np.zeros(len(note_index))
    for note in notes:
        if note in note_index:
            vector[note_index[note]] = 1
    return vector

def recommend_perfumes(fav_notes):
    fav_vector = notes_to_vector(fav_notes, note_index)
    distances, indices = knn.kneighbors([fav_vector])
    return indices[0]

def get_recommendations(fav_notes):
    print("Getting recommendations for:", fav_notes)
    indices = recommend_perfumes(fav_notes)
    print("Recommended indices:", indices)
    return indices.tolist()
