package com.wallscope.pronombackend.model;

import com.wallscope.pronombackend.utils.ModelUtil;
import com.wallscope.pronombackend.utils.RDFUtil;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;

import java.time.Instant;

import static com.wallscope.pronombackend.utils.RDFUtil.*;

public class Submission implements RDFWritable {

    private final Resource uri;
    private final Resource submissionType;
    private final Resource submissionStatus;
    private final Contributor submitter;
    private final Contributor reviewer;
    private final FileFormat source;
    private final TentativeFileFormat format;
    private final Instant created;
    private final Instant updated;


    public Submission(Resource uri, Resource submissionType, Resource submissionStatus, Contributor submitter, Contributor reviewer, FileFormat source, TentativeFileFormat format, Instant created, Instant updated) {
        this.uri = uri;
        this.submissionType = submissionType;
        this.submissionStatus = submissionStatus;
        this.submitter = submitter;
        this.reviewer = reviewer;
        this.source = source;
        this.format = format;
        this.created = created;
        this.updated = updated;
    }

    public String getID() {
        String[] parts = uri.getURI().split("/");
        return parts[parts.length - 1];
    }

    @Override
    public Resource getURI() {
        return uri;
    }

    @Override
    public Model toRDF() {
        Model m = ModelFactory.createDefaultModel();
        m.add(uri, makeProp(RDF.type), makeResource(PRONOM.Submission.type));
        if(submissionType != null) m.add(uri, makeProp(PRONOM.Submission.SubmissionType), submissionType);
        if(submissionStatus != null) m.add(uri, makeProp(PRONOM.Submission.SubmissionStatus), submissionStatus);
        if(source != null) m.add(uri, makeProp(PRONOM.Submission.Source), source.getURI());
        if(updated != null) m.add(uri, makeProp(PRONOM.Submission.Updated), makeXSDDateTime(updated));
        if(created != null) m.add(uri, makeProp(PRONOM.Submission.Created), makeXSDDateTime(created));
        if(submitter != null) {
            m.add(uri, makeProp(PRONOM.Submission.Contributor), submitter.getURI());
            m.add(submitter.toRDF());
        }
        if(reviewer != null) {
            m.add(uri, makeProp(PRONOM.Submission.Reviewer), reviewer.getURI());
            m.add(reviewer.toRDF());
        }
        if(source != null) {
            m.add(uri, makeProp(PRONOM.Submission.Source), source.getURI());
            m.add(source.toRDF());
        }
        if(format != null) {
            m.add(uri, makeProp(PRONOM.Submission.FileFormat), format.getURI());
            m.add(format.toRDF());
        }


        return m;
    }

    public Instant getUpdated() {
        return updated;
    }

    public Instant getCreated() {
        return created;
    }

    public Resource getSubmissionType() {
        return submissionType;
    }

    public Resource getSubmissionStatus() {
        return submissionStatus;
    }

    public Contributor getReviewer() {
        return reviewer;
    }

    public Contributor getSubmitter() {
        return submitter;
    }

    public FileFormat getSource() {
        return source;
    }

    public TentativeFileFormat getFormat() {
        return format;
    }

    @Override
    public String toString() {
        return "Submission{" +
                "uri=" + uri +
                ", submissionType=" + submissionType +
                ", submissionStatus=" + submissionStatus +
                ", submitter=" + submitter +
                ", reviewer=" + reviewer +
                ", source=" + source +
                ", format=" + format +
                ", created=" + created +
                ", updated=" + updated +
                '}';
    }

    public static class Deserializer implements RDFDeserializer<Submission> {

        @Override
        public Resource getRDFType() {
            return makeResource(RDFUtil.PRONOM.Submission.type);
        }

        @Override
        public Submission fromModel(Resource uri, Model model) {
            ModelUtil mu = new ModelUtil(model);
            Instant updated = safelyParseDateOrNull(safelyGetLiteralOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Submission.Updated))));
            Instant created = safelyParseDateOrNull(safelyGetLiteralOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Submission.Created))));
            Resource submissionType = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Submission.SubmissionType)));
            Resource submissionStatus = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Submission.SubmissionStatus)));
            Resource revRes = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Submission.Reviewer)));
            Contributor reviewer = null;
            if (revRes != null) {
                reviewer = new Contributor.Deserializer().fromModel(revRes, model);
            }
            Resource contRes = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Submission.Contributor)));
            Contributor contributor = null;
            if (contRes != null) {
                contributor = new Contributor.Deserializer().fromModel(contRes, model);
            }

            Resource sourceRes = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Submission.Source)));
            FileFormat source = null;
            if (sourceRes != null) {
                source = new FileFormat.Deserializer().fromModel(sourceRes, model);
            }

            Resource ffRes = safelyGetResourceOrNull(mu.getOneObjectOrNull(uri, makeProp(PRONOM.Submission.FileFormat)));
            FileFormat ff = null;
            if (ffRes != null) {
                ff = new FileFormat.Deserializer().fromModel(ffRes, model);
            }
            TentativeFileFormat tff = null;
            if (ff != null) {
                tff = new TentativeFileFormat(ff.getURI(), ff);
            }
            return new Submission(uri, submissionType, submissionStatus, contributor, reviewer, source, tff, created, updated);
        }
    }
}
