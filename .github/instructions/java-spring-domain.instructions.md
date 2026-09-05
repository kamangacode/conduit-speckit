---
applyTo: "**/domain/**/*.java, **/application/**/*.java"
---

# Terrain pédagogique Java/Spring : domaine et application

Cette instruction s'applique uniquement au terrain Java/Spring décrit dans `docs/sdd/04-chantier-conduit.md` ; elle ne crée pas une architecture Java active dans le dépôt documentaire.

Les dépendances doivent pointer vers l'intérieur. Le domaine reste indépendant de Spring, de JPA, de la base de données et des adaptateurs techniques. Les cas d'utilisation dépendent de ports et ne dépendent pas des contrôleurs.

Ne placer ni `@Entity`, ni `@Service`, ni `@Autowired` dans `domain/`. Les invariants métier vivent dans les agrégats et value objects ; les adaptateurs implémentent les ports depuis l'infrastructure.
