package com.megh.timekeeper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TimekeeperApplication

fun main(args: Array<String>) {
	runApplication<TimekeeperApplication>(*args)
}
