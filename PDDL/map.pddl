(define (problem map1)
  (:domain map-domain)

  (:objects
     agent1 agent2 - agent
     cell11 cell12 cell13 cell14 cell15 cell16 cell17 - cell
     keyb - key
     doorB - door
  )

  (:init
      (at agent1 cell11)
      (at agent2 cell14)
      (key-at keyb cell12)
      (door-between doorB cell15 cell16 keyb)
      (not (open doorB))

      (adjacent cell11 cell12)
      (adjacent cell12 cell13)
      (adjacent cell13 cell14)
      (adjacent cell14 cell15)
      (adjacent cell15 cell16)
      (adjacent cell16 cell17)

      (adjacent cell12 cell11)
      (adjacent cell13 cell12)
      (adjacent cell14 cell13)
      (adjacent cell15 cell14)
      (adjacent cell16 cell15)
      (adjacent cell17 cell16)

      (exit cell17)
  )

  (:goal 
    (exists (?ag - agent) (at ?ag cell17))
  )
)
