# pronom-ld-beta

A public repository for artifacts from the PRONOM Linked Data Beta Phase.

## Running Locally

To run the backend locally outside of Docker the following actions are required.

1. Build the frontend using npm v6 (node 12 or 14). This will copy the built
   frontend into `/backend/src/main/resources/`

```sh
cd frontend
npm i
npm run assemble
```

2. Run the fuseki container locally. The container currently stores the database
   within the image so we can grab it from the ECR repo. Restarting the
   container will reset the data to the original stored in the image.

```sh
docker run --rm -it  -d -p 3030:3030 --name fuseki -e ADMIN_PASSWORD=12345 -e ENABLE_DATA_WRITE=true -e ENABLE_UPDATE=false -e ENABLE_UPLOAD=false -e QUERY_TIMEOUT=60000 <image>
```

3. Create 2 directories `markdown` and `signatures`. In production these are
   mounted EFS shares at the root of the container but we can override the
   location by setting the `MARKDOWN_DIR` and `SIGNATURE_DIR` environment
   variables respectively. The `MARKDOWN_DIR` directory requires files already
   exist within it, run the following within this directory to create these.

```sh
touch about_accessibility-title.md about_pronom-documentation.md about_pronom-team.md contact_accessibility-title.md contact_details.md content-manager_accessibility-title.md contribute_accessibility-title.md contribute_how-do-we-process-your-submission.md contribute_how-to-submit-introduction.md contribute_how-to-submit-steps.md contribute_why-submit.md droid_accessibility-title.md droid_main.md error_accessibility-title.md external_accessibility-title.md external_projects.md faq_accessibility-title.md faq_main.md form-choice_accessibility-title.md form-choice_edit-format.md form-choice_new-format.md form-choice_other-enquiries.md index_accessibility-title.md index_main.md release-manager_accessibility-title.md release-notes_accessibility-title.md release-notes_release-notes.md release-notes_single-accessibility-title.md release-notes_test-enviroment-explanation.md release-notes_test-environment-title.md search_accessibility-title.md search_header.md search_main.md
```

4. Disable Keycloak authentication by setting the `KC_ENABLED` environment
   variable to `false`.
5. Optional - Change the port from 80 by setting the `PORT` environment
   variable.
6. Run the backend

```sh
cd backend
./gradlew bootRun
```

7. The app should now be running locally on `http://localhost:${PORT}`.

## Automated Deployment

Deployment is completed via GitHub Actions, when changes are pushed to the `main` branch of this repo the workflow is started. The workflow builds and pushes a new pronom-backend image to ECR and tags the commit with a unique version number. The workflow also tags the latest commit in the terraform repo with the same version number which begins the terraform apply workflow in that repo.
