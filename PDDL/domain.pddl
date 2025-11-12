(define (domain maze-domain)
  (:requirements :strips :typing :equality :existential-preconditions)

  (:types agent cell key door)

  (:predicates
    (at ?a - agent ?c - cell)
    (adjacent ?from - cell ?to - cell)
    (key-at ?k - key ?c - cell)
    (has-key ?a - agent ?k - key)
    (door-between ?d - door ?c1 - cell ?c2 - cell ?k - key)
    (open ?d - door)
    (exit ?c - cell)
  )

  ;; Accions

  ;; Moures
  (:action move
    :parameters (?a - agent ?from - cell ?to - cell)
    :precondition (and
      (at ?a ?from)
      (adjacent ?from ?to)
      (not (exists (?b - agent) (at ?b ?to)))

      (not (exists (?d - door ?k - key)
        (and (door-between ?d ?from ?to ?k)
             (not (open ?d))
        )
      ))
    )
    :effect (and
      (not (at ?a ?from))
      (at ?a ?to)
    )
  )

  ;; Recollir la clau
  (:action pick-key
    :parameters (?a - agent ?c - cell ?k - key)
    :precondition (and
      (at ?a ?c)
      (key-at ?k ?c)
    )
    :effect (and
      (has-key ?a ?k)
      (not (key-at ?k ?c))
    )
  )

  ;; Obrir la porta
  (:action open-door
    :parameters (?a - agent ?from - cell ?to - cell ?d - door ?k - key)
    :precondition (and
      (at ?a ?from)
      (adjacent ?from ?to)
      (door-between ?d ?from ?to ?k)
      (has-key ?a ?k)
      (not (open ?d))
    )
    :effect (open ?d)
  )

  ;; Moures a traves de la porta
  (:action move-through-door
    :parameters (?a - agent ?from - cell ?to - cell ?d - door ?k - key)
    :precondition (and
      (at ?a ?from)
      (adjacent ?from ?to)
      (door-between ?d ?from ?to ?k)
      (open ?d)
      (not (exists (?b - agent) (at ?b ?to)))
    )
    :effect (and
      (not (at ?a ?from))
      (at ?a ?to)
    )
  )
)

