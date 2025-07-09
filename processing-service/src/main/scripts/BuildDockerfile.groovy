// Read the Dockerfile template
String template = new File("${project.basedir}/src/main/docker/DockerfileTemplate".toString()).getText()

// Create Dockerfile template text from the file we read
def dockerFileText = new groovy.text.SimpleTemplateEngine().createTemplate(template)
        .make([fileName: project.build.finalName])

// Printing the information about the Dockerfile
println "writing dir " + "${project.basedir}/target/dockerfile"

// Create the Dockerfile directory
new File("${project.basedir}/target/dockerfile/".toString()).mkdirs()


println "writing file"

// Create the actual Dockerfile
File dockerFile = new File("${project.basedir}/target/dockerfile/Dockerfile".toString())

// Write the Dockerfile text to the file
dockerFile.withWriter('UTF-8') { writer ->
    writer.write(dockerFileText)
}