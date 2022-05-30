package com.wallscope.pronombackend

import com.wallscope.pronombackend.utils.RDFUtil.*
import org.apache.jena.datatypes.RDFDatatype
import org.apache.jena.datatypes.xsd.XSDDatatype
import org.apache.jena.rdf.model.*
import org.apache.jena.riot.Lang
import org.apache.jena.riot.RDFWriter
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

@Root(name = "ContainerSignatureMapping", strict = false)
class ContainerSignatureMapping {
    @field:Attribute(name = "schemaVersion", required = true)
    lateinit var schemaVersion: String

    @field:Attribute(name = "signatureVersion", required = true)
    lateinit var signatureVersion: String

    @field:ElementList(name = "ContainerSignatures", required = false)
    var signaturesList: List<ContainerSignature> = ArrayList()

    @field:ElementList(name = "FileFormatMappings", required = false)
    var ffMappings: List<FFMapping> = ArrayList()

    @field:ElementList(name = "TriggerPuids", required = false)
    var triggerPuids: List<TriggerPuid> = ArrayList()

}

@Root(strict = false)
class ContainerSignature {
    @field:Attribute(name = "Id", required = true)
    lateinit var id: String

    @field:Attribute(name = "ContainerType", required = true)
    lateinit var containerType: String

    @field:Element(name = "Description", required = false)
    var description: String? = null

    @field:ElementList(name = "Files", required = false)
    var fileList: List<FileObj> = ArrayList()
}

@Root(name = "File", strict = false)
class FileObj {
    @field:Element(name = "Path", required = false)
    var path: String? = null

    @field:Element(name = "BinarySignatures", required = false)
    var binarySignatures: BinarySignatures? = null
}

class BinarySignatures {
    @field:ElementList(name = "InternalSignatureCollection", required = false)
    var signatures: List<InternalSignature> = ArrayList()
}

@Root(name = "InternalSignature", strict = false)
class InternalSignature {
    @field:Attribute(name = "ID", required = true)
    var id: String? = null

    @field:ElementList(inline = true, required = false)
    var byteSequence: List<ByteSequence> = ArrayList()
}

@Root(name = "ByteSequence")
class ByteSequence {
    @field:Attribute(name = "Reference", required = false)
    var ref: String? = null

    @field:ElementList(inline = true, required = false)
    var subSeqs: List<SubSequence> = ArrayList()
}

@Root(name = "SubSequence", strict = false)
class SubSequence {
    @field:Attribute(name = "Position", required = false)
    var position: Int = 0

    @field:Attribute(name = "SubSeqMinOffset", required = false)
    var minOffset: Int? = null

    @field:Attribute(name = "SubSeqMaxOffset", required = false)
    var maxOffset: Int? = null

    @field:Element(name = "Sequence", required = true)
    var sequence: String? = null
}

@Root(name = "FileFormatMappings", strict = false)
class FFMapping {
    @field:Attribute(name = "signatureId", required = true)
    lateinit var sigId: String

    @field:Attribute(name = "Puid", required = true)
    lateinit var puid: String
}

@Root(name = "TriggerPuids", strict = false)
class TriggerPuid {
    @field:Attribute(name = "ContainerType", required = true)
    lateinit var type: String

    @field:Attribute(name = "Puid", required = true)
    lateinit var puid: String
}

fun refToURI(ref: String?) = when (ref) {
    "BOFoffset" -> URI(PRONOM.ByteSequence.AbsoluteFromBOF)
    "EOFoffset" -> URI(PRONOM.ByteSequence.AbsoluteFromEOF)
    "Variable" -> URI(PRONOM.ByteSequence.Variable)
    else -> null
}

fun convert(xmlFilePath: String, puidMapFilePath: String, outPath: String) {
    val serializer: Serializer = Persister()
    val mapping: ContainerSignatureMapping = serializer.read(ContainerSignatureMapping::class.java, File(xmlFilePath))
    System.err.println("XML File read successfully")

// read in puid map CSV
// associate puid => format id
    val puidMap = File(puidMapFilePath).readLines()
        .drop(1) // drop headers
        .associate {
            it.split(",").let { l -> l[1] to l[0] }
        } // split by comma and return as pair to associate into a map
    System.err.println("PUID CSV File read successfully")

// map signatures to their puids
    val sigMap = mapping.ffMappings.associate { it.sigId to it.puid }
    val model = jenaModel {
        mapping.signaturesList.forEach { cs ->

            val uri = URI(PRONOM.ContainerSignature.id) + cs.id
            add(uri.res, URI(RDF.type).prop, URI(PRONOM.ContainerSignature.type).res)
            add(uri.res, URI(RDFS.label).prop, cs.description ?: "")
            val containerTypeUri = URI(PRONOM.ContainerType.id + cs.containerType)
            add(uri.res, URI(PRONOM.ContainerSignature.ContainerType).prop, containerTypeUri.res)
            // Relate to file format and vice versa
            val puid = sigMap[cs.id] ?: throw Exception("Missing puid mapping for signature ${cs.id}")
            val ffId = puidMap[puid] ?: throw Exception("Missing file format mapping for puid $puid")
            val ffUri = URI(PRONOM.FileFormat.id) + ffId
            add(uri.res, URI(PRONOM.ContainerSignature.FileFormat).prop, ffUri.res)
            add(ffUri.res, URI(PRONOM.FileFormat.ContainerSignature).prop, uri.res)
            // process each different ContainerFile
            cs.fileList.forEachIndexed { i, fo ->
                val fileUri = URI(PRONOM.ContainerFile.id) + cs.id + ".$i"
                add(fileUri.res, URI(RDF.type).prop, URI(PRONOM.ContainerFile.type).res)
                // attach ContainerFile to ContainerSignature and vice versa
                add(uri.res, URI(PRONOM.ContainerSignature.ContainerFile).prop, fileUri.res)
                add(fileUri.res, URI(PRONOM.ContainerFile.ContainerSignature).prop, uri.res)
                // add FilePath
                fo.path?.let { add(fileUri.res, URI(PRONOM.ContainerFile.FilePath).prop, it.toRDFLiteral()) }
                // process each bytesequence
                fo.binarySignatures?.signatures?.forEach binarySigs@{ sig ->
                    if (sig.id == null) return@binarySigs
                    sig.byteSequence.forEach byteSeqs@{ bs ->
                        val bsUri = URI(PRONOM.ByteSequence.id) + cs.id + ".$i." + sig.id!!
                        add(bsUri.res, URI(RDF.type).prop, URI(PRONOM.ByteSequence.type).res)
                        // return if there's no subsequence objects
                        val subSeq = bs.subSeqs.firstOrNull() ?: return@byteSeqs
                        // attach ByteSequence to ContainerFile and vice versa
                        add(fileUri.res, URI(PRONOM.ContainerFile.ByteSequence).prop, bsUri.res)
                        add(bsUri.res, URI(PRONOM.ByteSequence.ContainerFile).prop, fileUri.res)

                        // Add the sequence
                        subSeq.sequence?.let {
                            add(bsUri.res, URI(PRONOM.ByteSequence.ByteSequence).prop, it.toRDFLiteral())
                        }

                        // transform and add the position
                        // Here we are assuming all positions are Absolute from BOF/EOF
                        // We have no way to tell if they are relative
                        refToURI(bs.ref)?.let {
                            add(bsUri.res, URI(PRONOM.ByteSequence.ByteSequencePosition).prop, it.res)
                        }
                        // minOffset if it's not null
                        subSeq.minOffset?.let {
                            add(bsUri.res, URI(PRONOM.ByteSequence.Offset).prop, "$it".toRDFLiteral(XSDDatatype.XSDint))
                        }
                        // maxOffset if it's not null
                        subSeq.maxOffset?.let {
                            add(
                                bsUri.res,
                                URI(PRONOM.ByteSequence.MaxOffset).prop,
                                "$it".toRDFLiteral(XSDDatatype.XSDint)
                            )
                        }
                        // The RDF model also specifies the following properties
                        // which were not found in any instance in the XML files
                        // and are therefore deemed safe to ignore
                        // ByteOrder IndirectOffsetLocation IndirectOffsetLength
                    }
                }
            }
        }
        // Register the 2 different Container Types
        listOf("ZIP", "OLE2").forEach { type ->
            val uri = URI(PRONOM.ContainerType.id + type)
            add(uri.res, URI(RDF.type).prop, URI(PRONOM.ContainerType.type).res)
            add(uri.res, URI(RDFS.label).prop, type.toRDFLiteral())
            mapping.triggerPuids.filter { it.type == type }.forEach {
                val ffId = puidMap[it.puid] ?: throw Exception("Missing file format mapping for puid ${it.puid}")
                val ffUri = URI(PRONOM.FileFormat.id) + ffId
                add(uri.res, URI(PRONOM.ContainerType.FileFormat).prop, ffUri.res)
            }
        }

    }
    RDFWriter.create().lang(Lang.TURTLE).source(model).output(outPath)
}