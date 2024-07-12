fun includeModule(name:String) {
    include(":$name")
    project(":$name").name = "parser_tester_$name"
}

includeModule("api")


