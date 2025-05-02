# Instructions for Running the Application

## Overview
This project processes query XML files, transforms them into view XML files, and generates output JSON files using a catalog XML file. The workflow is managed using a configuration file (`config/config.xml`).

---

## Directory Structure

```
CrossDataViews/
├── lib/
│   ├── gson-2.10.1.jar
│   ├── basex-api-10.5.jar
│   ├── mysql-connector-java-8.0.33.jar
├── src/
│   ├── Main.java
│   ├── QueryToView.java
│   ├── ViewEngine.java
│   ├── db/...
│   ├── model/...
│   ├── engine/...
├── config/
│   ├── config.xml
│   ├── catalog.xml
├── queries/
│   ├── ...
├── viewDefinitions/
│   ├── ...
├── outputs/
│   ├── ...
```

---

## Configuration File (`config/config.xml`)
The configuration file specifies the paths for the input and output files. Below is an example:

```xml
<Configuration>
    <QueryFilePath>queries/query2.xml</QueryFilePath>
    <ViewFilePath>viewDefinitions/view2.xml</ViewFilePath>
    <CatalogFilePath>config/catalog.xml</CatalogFilePath>
    <OutputFilePath>outputs/output2.json</OutputFilePath>
</Configuration>
```

### File Details:
1. **QueryFilePath**: Path to the query XML file (e.g., `queries/query2.xml`).
2. **ViewFilePath**: Path to save the transformed view XML file (e.g., `viewDefinitions/view2.xml`).
3. **CatalogFilePath**: Path to the catalog XML file (e.g., `config/catalog.xml`).
4. **OutputFilePath**: Path to save the final output JSON file (e.g., `outputs/output2.json`).

---


## How to Run the Program
1. **Compile the Java Files**:
   ```bash
   javac -cp "lib/*" -d bin src/*.java src/db/*.java src/model/*.java src/engine/*.java
   ```

2. **Run the Program**:
   Pass the configuration file path as an argument:
   ```bash
   java -cp "bin:lib/*" Main 
   ```

3. **Output**:
   - The transformed view XML will be saved to the path specified in `<ViewFilePath>`.
   - The final output JSON will be saved to the path specified in `<OutputFilePath>`.

