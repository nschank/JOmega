/**
 * Model check for the Cache data type.
 * nschank.collect.map.Cache
 */

sig Cache
{
	size : Int,
	nodes : set Node,
	next : Node->Node
}
{ size >= 1 }

sig Node {}

pred inv[c : Cache]
{
	Node.(c.next + ~(c.next)) = c.nodes
	no iden & ^(c.next - iden)
	(c.nodes <: iden) in c.next
	#(c.nodes) <= c.size
}

pred add[c, c': Cache, n : Node]
{
	inv[c]
	c.size = c'.size
	
	n in c.nodes and n = c.first => c=c'
	n in c.nodes and n != first[c] => {
		c.nodes = c'.nodes
		c'.first = n
		c'.next = c'.nodes <: c.next + n->first[c] + ~(c.next).n->c.next.n :> c'.nodes
	}
	
	n not in c.nodes and c.size > #c.nodes => {
		c'.nodes = c.nodes + n
		c'.next = c.next + n->first[c] + n->n
	}
	n not in c.nodes and c.size = #c.nodes => {
		c'.nodes = c.nodes + n - last[c]
		c'.next = c'.nodes <: (c.next + n->first[c] + n->n) :> c'.nodes
	}
}

pred access[c, c' : Cache, n : Node]
{
	inv[c]
	c.size = c'.size
	c.nodes = c'.nodes
	n = c.first => c.next = c'.next
	n != c.first => c'.next = c.next + n->c.first + n.~(c.next)->n.(c.next) - n.~(c.next)->n - n->n.(c.next)
}

fun Cache.first: Node
{
	{n : this.nodes | one (n <: ~(this.next))}
}

fun Cache.last: Node
{
	{n : this.nodes | one (n <: (this.next))}
}

assert AddPreservesInvariant
{
	all c, c' : Cache | all n : Node | add[c,c',n] => inv[c']
}
check AddPreservesInvariant for 10 but 5 int, exactly 2 Cache

assert AccessPreservesInvariant
{
	all c, c' : Cache | all n : Node | access[c,c',n] => inv[c']
}
check AccessPreservesInvariant for 1 but 5 int, exactly 2 Cache
