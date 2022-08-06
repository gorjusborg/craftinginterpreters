# Crafting interpreter notes

## 2022-07-16

### Chapter 6, recursive descent parser

How does recursive descent parser work? 

Why does parser try to match lowest precendence first?

	Because the parser wants the lowest precendence operators to match last, so
	they can appear highest in the parse tree. The precendence methods will return expressions of 
    their specified precendence or higher. The expression will be the widest possible given the 
    token stream.

Why does parser stay on same or higher precedence until it can no longer match? 
	
    Because each call to an expression method will return the widest tree
    possible at the current precendence level or higher. Also, it is possible
    that two expressions at the same level of precendence are next to each other.
    The precedence rules (specifically associativity rules) dictate that they 
    must be applied in in left-first or right-first fashion. 
