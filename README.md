# maven-plantuml-plugin

---

Apache Maven PlantUML plugin to convert diagrams as a code to images.

---

## Getting started

### Build

```shell
./mvnw clean install
```

### Run


```shell
./mvnw com.github.ravlinko:plantuml-maven-plugin:build

# Short version
./mvnw plantuml:build
```

### Dependency upgrade list

```shell
./mvnw versions:display-plugin-updates
./mvnw versions:display-dependency-updates
```

### Dependency vulnerability scan
```shell
./mvnw -B -ntp dependency-check:check
```

### Publish site

```shell
./mvnw clean site site:stage scm-publish:publish-scm
```