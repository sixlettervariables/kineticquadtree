# Kinetic Quadtree
Kinetic Hybrid PR (Point-Region) and PMR (Polygon-Map-Random) Quad Tree. This class is a
working framework for a Kinetic PR/PMR Quad tree. Basically your standard Quad tree is
static and filled once, or filled continuously without any removals. The Kinetic PR/PMR
Quad tree takes into account objects in motion, along with static objects. Each object
in the quad tree extends the Observable abstract class, which allows the quad tree nodes
to be notified when an object moves in the coordinate space.

## Implementation

### Introduction
Kinetic Hybrid PR (Point-Region) and PMR (Polygon-Map-Random) Quad Tree. This class is a
working framework for a Kinetic PR/PMR Quad tree. Basically your standard Quad tree is
static and filled once, or filled continuously without any removals. The Kinetic PR/PMR
Quad tree takes into account objects in motion, along with static objects. Each object in
the quad tree extends the Observable abstract class, which allows the quad tree nodes to
be notified when an object moves in the coordinate space.

### Details
The PMR portion of the Quad tree stores a pointer to a polygon into every node the polygon
reports it crosses. Currently this allows for efficient lookups of what lines are in what
bounding boxes. However, collision detection is not quite up to par as the entire polyline
is checked during collision detection rather than just the segment insidethe bounding area.
Detaching and reattaching segments with respect to the kinetic nature of the tree would be
cumbersome and slow. The main issue is with overly complex polylines which double back on
themselves multiple times over.

The kinetic nature of this quad tree only takes action when the update notice informs a node
that an object has moved outside the node's boundaries. When this happens a search up the tree
is taken for a parent node which now bounds the object. This node is then given ownership and
the node is added as if it was freshly inserted. This approach allows for fast inserts as nodes
move between adjacent nodes a majority of the time, so in the average case the search only needs
to look up to it's parent and at most one more parent higher.

After an object moves from one node to another, the parent added the object then calls balance,
which takes a look at the new sub-structure. If the new sub-structure is too large for the number
of nodes it contains, it removes all of the children and becomes a leaf. If it contains too few,
it looks to every leaf or child containing a leaf and applies balance, until all nodes report
too-few objects. Initially I was worried about thrashing in the data structure, i.e. balance
occuring too often, however, empirical tests of 4000 objects contained in a 500x500 area showed
that there was no slow down in performance (for a machine that could reasonably be expected to
render 4000 objects moving in random directions or in guided directions). Even when restrictions
on collisions were introduced, the quad tree algorithm allowed for efficient lookups and detection,
making this additional step a non-issue. My reference for this data structure was a paper and demo
by Ransom Kershaw Winder (2 Dec 2000) entitled "The Kinetic PR Quadtree". His implementation makes
use of prio-queues and MVCC tags to ensure data is updated in order, along with the structure of
the tree. His implementation provides for a thread safe version, allowing the object update thread
or threads to operate independently of the quadtree balancing thread. With MVCC-tagging the problem
of out-of-order or invalid updates are resolved with a simple age parameter. The prio-queue ignores
all entries who's objects have a larger age than the key of the entry. With this setup, the tree
can effectively schedule its balances en masse and at its own discresion. While I initially attempted
to implement this idea, I found that context switching between two threads and the addition of
multiple schedules along with synchronization to be far more of an issue than performance in both
terms of time and in necessity.

- [Winder, Ransom Kershaw. "The Kinetic Quadtree." 2 Dec. 2000.](http://www.cs.umd.edu/~mount/Indep/Ransom/)
