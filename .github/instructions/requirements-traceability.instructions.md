---
applyTo: "docs/prd/**/*.md, docs/requirements/**/*.md, specs/**/*.md, **/*.spec.ts, **/*.test.ts, **/src/test/**/*.java"
---

# Exigences et traçabilité

Une exigence possède un identifiant stable, un statut explicite et des critères d'acceptation observables. Les critères décrivent un contexte, une action et un résultat attendu ; remplacer les formulations vagues par un comportement vérifiable.

Une feature Spec Kit relie `spec.md`, `plan.md`, `tasks.md` et les preuves produites. Une tâche sans exigence, décision ou nécessité technique identifiable est du scope non tracé et doit être clarifiée avant implémentation.

Une exigence marquée comme implémentée doit pointer vers les fichiers et preuves qui la réalisent. Un lien vers un fichier inexistant ou un test qui ne contrôle pas le comportement attendu ne constitue pas une preuve.

Les exigences produit viennent de `docs/prd/`, la méthode vient de `docs/sdd/` et les décisions structurantes viennent des ADR. Ne pas dupliquer une exigence dans plusieurs sources sans lien explicite entre elles.
