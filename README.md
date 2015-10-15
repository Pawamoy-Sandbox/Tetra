# Tetranucleo

[![Join the chat at https://gitter.im/Ecole-des-collegues/Tetranucleo](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Ecole-des-collegues/Tetranucleo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Fonction de croissance des codes circulaires de tétranucléotides autocomplémentaires.

## Libs

* JGraphT
* Apache Commons Math3
* dpaukov Combinatorics

## Optimisations possibles

- Utiliser des Enum pour tous les tetras (et splits de tetras) du code ? Itérer sur un Enum demande d'utiliser .values()
  qui renvoie un tableau (et donc crée un tableau à chaque fois), ce n'est peut-être pas plus performant qu'un et un
  seul tableau de String/Integer initialisé une bonne fois pour toute. A utiliser donc seulement quand on n'a pas à
  itérer sur l'enum.
- Utilisation de Disruptor (voir issues)
- Multi-threading sur la génération de graphes (1/3 - 3/1 et 2/2)
- Les types primitifs sont plus performants, mais l'autoboxing a un impact sur les perfs.
  Si on pouvait réimplémenter les graphes avec des int, on pourrait gagner en perfs (vrai pour tout le code en fait).
- Multi-threading sur le producteur (plusieurs producteurs)
- Compression des résultats (écriture de fichiers compressés)
- Découpage de la HashMap en plusieurs HashMap (une par longueur de code) ?
  Un petit test a montré que le temps d'accès à l'élément d'une HashMap de 100.000+ est inférieur à 1ms
- Construction des chaînes de caractères (éviter les recopies via +)
- Une HashMap globale en lecture seule, une HashMap par consommateur en écriture,
  fusion des HashMap partielle dans la globale à la fin des threads (plus sécurisé/rapide?)
- Utiliser l'executor de Guava (voir le 2ème post de [cette page](https://stackoverflow.com/questions/2247734/executorservice-standard-way-to-avoid-to-task-queue-getting-too-full))

## Questions

- Est-il utile ou contre-performant d'utiliser plusieurs niveaux de threads ?
  (plusieurs producteurs qui lancent plusieurs consommateurs qui lancent plusieurs calculs de graphes...)
  Au final le processeur gère correctement un certains nombre de threads, il faut éviter la surcharge.
