package task12.other

import task12.CustomError

// CustomError is an open class, so it can be extended outside task12 package
class Overheated : CustomError("The disk is too hot")
