package com.example.security

import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import java.security.SecureRandom

fun getHashWithSalt(stringToHash: String, saltLength: Int = 32): String{
    val salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLength)
    val saltAsHex = Hex.encodeHex(salt)
    val hash = DigestUtils.sha256Hex("$saltAsHex$stringToHash")
    return "$saltAsHex:$hash"
}

fun checkHashForPassword(inputPassword: String, hashWithSalt: String): Boolean {
    val hashAndSalt = hashWithSalt.split(":")
    val salt = hashAndSalt[0]
    val hash = hashAndSalt[1]

    val passwordHash = DigestUtils.sha256Hex("$salt$inputPassword")
    return hash == passwordHash
}