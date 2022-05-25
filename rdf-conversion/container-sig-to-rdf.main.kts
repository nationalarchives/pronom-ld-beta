#!/usr/bin/env kotlin

@file:DependsOn("org.apache.jena:jena-core:4.5.0")
@file:DependsOn("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
@file:DependsOn("org.simpleframework:simple-xml:2.7.1")

import org.apache.jena.datatypes.RDFDatatype
import org.apache.jena.rdf.model.*
import org.simpleframework.xml.*
import org.simpleframework.xml.core.Persister
import java.io.File

fun jenaModel(block: Model.() -> Unit): Model = ModelFactory.createDefaultModel().apply(block)
fun String.toRDFLiteral() = ResourceFactory.createStringLiteral(this)!!
fun String.toRDFLiteral(type: RDFDatatype) = ResourceFactory.createTypedLiteral(this, type)!!
fun Model.list(s: Resource? = null, p: Property? = null, o: RDFNode? = null) = this.listStatements(s, p, o).toList()

class URI(val value: String) {
    operator fun plus(rs: String) = URI(value + rs)
    operator fun plus(rs: URI) = URI(value + rs.value)
    val res
        get() = ResourceFactory.createResource(value)!!
    val prop
        get() = ResourceFactory.createProperty(value)!!

    override fun toString() = value
}

object RDF {
    val uri = URI("http://www.w3.org/1999/02/22-rdf-syntax-ns#")
    val type = uri + "type"

    object WALLS {
        val uri = URI("http://wallscope.co.uk/ontology/")
        val nlpType = uri + "nlp/type"
        val nlpText = uri + "nlp/text"
        val startTime = uri + "startTime"
        val endTime = uri + "endTime"
    }

    object DCT {
        val uri = URI("http://purl.org/dc/terms/")
        val subject = uri + "subject"
        val relation = uri + "relation"
    }
}

@Root(name = "ContainerSignatureMapping", strict = false)
class ContainerSignatureMapping {
    @field:Attribute(name = "schemaVersion", required = false)
    var schemaVersion: String? = null

    @field:Attribute(name = "signatureVersion", required = false)
    var signatureVersion: Int? = null

    @field:Element(name = "ContainerSignatures", required = false)
    var signaturesList: ContainerSignatureDef? = null

    @field:ElementList(name = "FileFormatMappings", required = false)
    var ffMappings: List<FFMapping> = ArrayList()

    @field:ElementList(name = "TriggerPuids", required = false)
    var triggerPuids: List<TriggerPuid> = ArrayList()

}

class ContainerSignatureDef {
    @field:ElementList(name = "ContainerSignature", required = false)
    var signatures: List<ContainerSignature> = ArrayList()
}

class ContainerSignature {
    @field:Attribute(name = "Id", required = false)
    var id: String? = null
    @field:Attribute(name = "ContainerType", required = false)
    var containerType: String? = null

    @field:Element(name = "Description", required = false)
    var description: String? = null

    @field:ElementList(name = "Files", inline = true, entry = "File", type = FileObj::class)
    var fileList: List<FileObj> = ArrayList()
}

class FilesList {
    @field:ElementList(name = "File", required = false)
    var files: List<FileObj> = ArrayList()
}

class FileObj {
    @field:Element(name = "Path", required = false)
    var path: String? = null

    @field:Element(name = "BinarySignatures", required = false)
    var binarySignatures: BinarySignatures? = null
}

class BinarySignatures {
    @field:Element(name = "InternalSignatureCollection", required = false)
    var collection: InternalSignatureCollection? = null
}

class InternalSignatureCollection {
    @field:ElementList(name = "InternalSignature", required = false)
    var signatures: List<InternalSignature> = ArrayList()
}

class InternalSignature {
    @field:Attribute(name = "ID", required = false)
    var id: String? = null

    @field:Element(name = "ByteSequence", required = false)
    var byteSequence: ByteSequence? = null
}

class ByteSequence {
    @field:Attribute(name = "Reference", required = false)
    var ref: String? = null

    @field:ElementList(name = "SubSequence", required = false)
    var subSeqs: List<SubSequence> = ArrayList()
}

class SubSequence {
    @field:Attribute(name = "Position", required = false)
    var position: Int? = null

    @field:Attribute(name = "SubSeqMinOffset", required = false)
    var minOffset: Int? = null

    @field:Attribute(name = "SubSeqMaxOffset", required = false)
    var maxOffset: Int? = null

    @field:Element(name = "Sequence", required = false)
    var sequence: String? = null
}

class FFMapping {
    @field:Attribute(name = "signatureId", required = false)
    var sigId: Int? = null
    @field:Attribute(name = "Puid", required = false)
    var puid: String? = null
}

class TriggerPuid {
    @field:Attribute(name = "ContainerType", required = false)
    var type: String? = null
    @field:Attribute(name = "Puid", required = false)
    var puid: String? = null
}

val XML_FILE_PATH = System.getenv("XML_FILE_PATH") ?: "data/container-signature-20220311.xml"

val serializer: Serializer = Persister()
val mapping = serializer.read(ContainerSignatureMapping::class.java, File(XML_FILE_PATH))

println(mapping.signaturesList)