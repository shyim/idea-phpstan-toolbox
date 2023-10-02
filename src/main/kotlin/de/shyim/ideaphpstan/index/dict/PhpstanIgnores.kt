package de.shyim.ideaphpstan.index.dict

import org.apache.commons.lang3.builder.HashCodeBuilder
import java.io.Serializable
import java.util.*

class PhpstanIgnores(public var phpStanFile: String, public var errors: Int, public var start: Int, public var end: Int) : Serializable {
    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(this.phpStanFile)
            .append(this.errors)
            .append(this.start)
            .append(this.end)
            .toHashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is PhpstanIgnores &&
                Objects.equals(other.phpStanFile, this.phpStanFile) &&
                Objects.equals(other.errors, this.errors) &&
                Objects.equals(other.start, this.start) &&
                Objects.equals(other.end, this.end)
    }
}
