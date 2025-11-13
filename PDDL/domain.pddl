(define (domain map-domain)
  (:requirements :strips :typing :equality :existential-preconditions)

  (:types agent cell key door)

  (:predicates
    (at ?ag - agent ?c - cell)
    (adjacent ?c1 - cell ?c2 - cell)
    (key-at ?k - key ?c - cell)
    (has-key ?ag - agent ?k - key)
    (door-between ?d - door ?c1 - cell ?c2 - cell ?k - key)
    (open ?d - door)
    (exit ?c - cell)
    (occupied ?c - cell)
  )

  (:action move
    :parameters (?ag - agent ?from - cell ?to - cell)
    :precondition (and
      (at ?ag ?from)
      (adjacent ?from ?to)
      (not (occupied ?to))
      (not (exists (?d - door ?k - key)
        (and (door-between ?d ?from ?to ?k)
             (not (open ?d))
        )
      ))
    )
    :effect (and
      (not (at ?ag ?from))
      (at ?ag ?to)
      (not (occupied ?from))
      (occupied ?to)
    )
  )

  (:action move-pick-key
    :parameters (?ag - agent ?from - cell ?to - cell ?k - key)
    :precondition (and
      (at ?ag ?from)
      (adjacent ?from ?to)
      (key-at ?k ?to)
      (not (occupied ?to))
    )
    :effect (and
      (not (at ?ag ?from))
      (at ?ag ?to)
      (not (occupied ?from))
      (occupied ?to)
      (has-key ?ag ?k)
      (not (key-at ?k ?to))
    )
  )

  (:action open-door
    :parameters (?ag - agent ?from - cell ?to - cell ?d - door ?k - key)
    :precondition (and
      (at ?ag ?from)
      (adjacent ?from ?to)
      (door-between ?d ?from ?to ?k)
      (has-key ?ag ?k)
      (not (open ?d))
    )
    :effect (open ?d)
  )

  (:action move-through-open-door
    :parameters (?ag - agent ?from - cell ?to - cell ?d - door ?k - key)
    :precondition (and
      (at ?ag ?from)
      (adjacent ?from ?to)
      (door-between ?d ?from ?to ?k)
      (open ?d)
      (not (occupied ?to))
    )
    :effect (and
      (not (at ?ag ?from))
      (at ?ag ?to)
      (not (occupied ?from))
      (occupied ?to)
    )
  )

  (:action move-to-exit
    :parameters (?ag - agent ?from - cell ?to - cell)
    :precondition (and
      (at ?ag ?from)
      (adjacent ?from ?to)
      (exit ?to)
      (not (occupied ?to))
    )
    :effect (and
      (not (at ?ag ?from))
      (at ?ag ?to)
      (not (occupied ?from))
      (occupied ?to)
    )
  )
)
