---
applyTo: "**/*"
---

# Reproductibilité et mémoire des décisions

Les règles versionnées sont la source unique des conventions actives. Une convention ne doit pas être redéfinie différemment dans une autre instruction, un prompt, une documentation ou un commentaire.

Classer chaque information dans le bon registre : une décision structurante dans un ADR, une leçon issue d'un échec dans le journal ou les lessons, une observation de méthode dans `docs/sdd/journal.md`, et l'état d'un cycle dans les artefacts `specs/`.

Toute nouvelle pratique doit d'abord produire un signal observable et non bloquant, être calibrée sur des cas réels, puis devenir un gate seulement lorsque son bruit et ses faux positifs sont connus.

Un lecteur qui ne possède pas l'historique de la conversation doit pouvoir reconstruire l'intention, le périmètre et la validation d'un changement à partir des fichiers versionnés.
