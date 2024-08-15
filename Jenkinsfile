@Library('msb-library@bpm-dso')_
my_node = k8sagent(name: 'maven3.8.6.jdk8')
podTemplate(my_node) {
    node(my_node.label) {
        env.PROJECT_NAME = "bpm"
        env.SERVICE_NAME = "approval-service"
        bpm_backend()
    }
}