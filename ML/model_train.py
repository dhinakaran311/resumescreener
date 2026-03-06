"""
ATS Resume Screener — ML Training Script (Phase 16)
=====================================================
Dataset  : ML/dataset.csv  (1000 rows, 11 features + label)
Algorithm: Logistic Regression (scikit-learn)
Output   : ML/trained_model.onnx  (loaded by Java ATS in Phase 17)

Run:
    pip install scikit-learn skl2onnx onnx pandas numpy
    python ML/model_train.py
"""


import os
import numpy as np
import pandas as pd
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.metrics import (
    accuracy_score, classification_report, confusion_matrix
)

# ─── 1. LOAD DATASET ────────────────────────────────────────────────────────

SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
DATASET_PATH = os.path.join(SCRIPT_DIR, "..", "ats_training_dataset_1000.csv")
MODEL_PATH   = os.path.join(SCRIPT_DIR, "trained_model.onnx")

FEATURE_COLS = [
    "tfidfScore", "skillScore", "matchedCount", "missingCount",
    "experienceYears", "programmingScore", "backendScore",
    "frontendScore", "databaseScore", "mlAiScore", "dataScienceScore", "cloudScore", "devopsScore", "toolsScore"
]
LABEL_COL = "label"

print("=" * 60)
print("  ATS Resume Screener — ML Training (Phase 16)")
print("=" * 60)

df = pd.read_csv(DATASET_PATH)
print(f"\n[1] Dataset loaded: {len(df)} rows, {len(df.columns)} columns")

# ─── 2. VALIDATE COLUMNS ────────────────────────────────────────────────────

missing_cols = [c for c in FEATURE_COLS + [LABEL_COL] if c not in df.columns]
if missing_cols:
    raise ValueError(f"Missing columns in dataset: {missing_cols}")
print(f"    Columns OK: {FEATURE_COLS + [LABEL_COL]}")

# ─── 3. LABEL BALANCE CHECK ─────────────────────────────────────────────────

label_counts = df[LABEL_COL].value_counts().sort_index()
total = len(df)
print(f"\n[2] Label Balance:")
for lbl, cnt in label_counts.items():
    tag = "good candidate" if lbl == 1 else "bad candidate"
    print(f"    label={lbl} ({tag}): {cnt} rows  ({cnt/total*100:.1f}%)")

imbalance_ratio = label_counts.max() / label_counts.min()
if imbalance_ratio > 2.0:
    print(f"    [WARNING] Imbalance ratio {imbalance_ratio:.1f}x detected -> using class_weight='balanced'")
    class_weight = "balanced"
else:
    print(f"    [OK] Balance looks good (ratio {imbalance_ratio:.1f}x)")
    class_weight = None

# ─── 4. REMOVE OUTLIERS ─────────────────────────────────────────────────────

RANGES = {
    "tfidfScore":      (0, 100),
    "skillScore":      (0, 100),
    "matchedCount":    (0, 50),
    "missingCount":    (0, 50),
    "experienceYears": (0, 15),
    "programmingScore":(0, 100),
    "backendScore":    (0, 100),
    "frontendScore":   (0, 100),
    "databaseScore":   (0, 100),
    "mlAiScore":       (0, 100),
    "dataScienceScore":(0, 100),
    "cloudScore":      (0, 100),
    "devopsScore":     (0, 100),
    "toolsScore":      (0, 100),
}

original_len = len(df)
for col, (lo, hi) in RANGES.items():
    df = df[(df[col] >= lo) & (df[col] <= hi)]
removed = original_len - len(df)
print(f"\n[3] Outlier Removal: {removed} rows removed -> {len(df)} rows remain")

# ─── 5. TRAIN / TEST SPLIT ──────────────────────────────────────────────────

X = df[FEATURE_COLS].values
y = df[LABEL_COL].values

X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y
)
print(f"\n[4] Train/Test Split (80/20, stratified):")
print(f"    Train: {len(X_train)} rows | Test: {len(X_test)} rows")

# ─── 6. BUILD & TRAIN PIPELINE ──────────────────────────────────────────────

pipeline = Pipeline([
    ("scaler", StandardScaler()),
    ("clf",    LogisticRegression(
        class_weight=class_weight,
        max_iter=1000,
        random_state=42,
        solver="lbfgs"
    ))
])

print(f"\n[5] Training Logistic Regression ...")
pipeline.fit(X_train, y_train)
print("    [OK] Training complete")

# ─── 7. EVALUATE ────────────────────────────────────────────────────────────

y_pred = pipeline.predict(X_test)
acc = accuracy_score(y_test, y_pred)

print(f"\n[6] Evaluation on Test Set ({len(X_test)} rows):")
print(f"    Accuracy : {acc * 100:.2f}%")
print(f"\n    Classification Report:")
print(classification_report(y_test, y_pred, target_names=["Bad (0)", "Good (1)"]))

cm = confusion_matrix(y_test, y_pred)
print(f"    Confusion Matrix:")
print(f"                 Predicted 0   Predicted 1")
print(f"    Actual 0  :  {cm[0,0]:>10}    {cm[0,1]:>10}")
print(f"    Actual 1  :  {cm[1,0]:>10}    {cm[1,1]:>10}")

# ─── 8. EXPORT TO ONNX ──────────────────────────────────────────────────────

try:
    from skl2onnx import convert_sklearn
    from skl2onnx.common.data_types import FloatTensorType

    initial_type = [("float_input", FloatTensorType([None, len(FEATURE_COLS)]))]
    onnx_model = convert_sklearn(pipeline, initial_types=initial_type)

    with open(MODEL_PATH, "wb") as f:
        f.write(onnx_model.SerializeToString())

    print(f"\n[7] ONNX Model exported -> {MODEL_PATH}")
    print("    [OK] Ready for Java ATS Phase 17 integration")

except ImportError:
    print("\n[7] [WARNING] skl2onnx not installed — ONNX export skipped.")
    print("    To install: pip install skl2onnx onnx")
    print("    Saving as pickle fallback instead ...")

    import pickle
    pickle_path = os.path.join(SCRIPT_DIR, "trained_model.pkl")
    with open(pickle_path, "wb") as f:
        pickle.dump(pipeline, f)
    print(f"    [OK] Pickle model saved -> {pickle_path}")

print("\n" + "=" * 60)
print("  Phase 16 Complete! Model is ready.")
print("=" * 60)
