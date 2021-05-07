package com.rti.charisma.api.exception

class ContentServerException(msg: String, exception: Exception): RuntimeException(msg, exception ) {}
