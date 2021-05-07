package com.rti.charisma.api.exception

class ContentException(msg: String, exception: Exception): RuntimeException(msg, exception ) {}
