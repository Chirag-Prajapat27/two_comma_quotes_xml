package com.app.twocommaquotes.model

class BaseModel<T>(
    val status: Int, val message: String, val result: HashMap<String, T>
)