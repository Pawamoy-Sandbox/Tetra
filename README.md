# Tetranucleo

[![Join the chat at https://gitter.im/Ecole-des-collegues/Tetranucleo](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/Ecole-des-collegues/Tetranucleo?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Fonction de croissance des codes circulaires de tétranucléotides autocomplémentaires.

## Libs

* JGraphT

## Optimisations possibles

- Utiliser des Enum pour tous les tetras (et splits de tetras) du code ? Itérer sur un Enum demande d'utiliser .values()
  qui renvoie un tableau (et donc crée un tableau à chaque fois), ce n'est peut-être pas plus performant qu'un et un
  seul tableau de String/Integer initialisé une bonne fois pour toute. A utiliser donc seulement quand on n'a pas à
  itérer sur l'enum.
- Utilisation de Disruptor (voir issues)
- Les types primitifs sont plus performants, mais l'autoboxing a un impact sur les perfs.
  Si on pouvait réimplémenter les graphes avec des int, on pourrait gagner en perfs (vrai pour tout le code en fait).
- Compression des résultats (écriture de fichiers compressés)
- Construction des chaînes de caractères (éviter les recopies via +)
- Utiliser l'executor de Guava (voir le 2ème post de [cette page](https://stackoverflow.com/questions/2247734/executorservice-standard-way-to-avoid-to-task-queue-getting-too-full))

