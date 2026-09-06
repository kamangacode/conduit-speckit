#!/usr/bin/env ruby

require "fileutils"
require "yaml"

COMMANDS = %w[validate generate].freeze

def usage
  warn <<~USAGE
    Usage:
      ruby scripts/functional-tests.rb validate <feature-directory>
      ruby scripts/functional-tests.rb generate <feature-directory> <output-directory>
  USAGE
  exit 2
end

command = ARGV.shift
usage unless COMMANDS.include?(command)

feature_directory = ARGV.shift
usage unless feature_directory

manifest_path = File.join(feature_directory, "test-cases.yaml")
spec_path = File.join(feature_directory, "spec.md")
abort "Missing manifest: #{manifest_path}" unless File.file?(manifest_path)
abort "Missing specification: #{spec_path}" unless File.file?(spec_path)

manifest = YAML.load_file(manifest_path)
cases = manifest.fetch("cases")
specification = File.read(spec_path)
case_ids = cases.map { |test_case| test_case.fetch("id") }
requirement_ids = specification.scan(/FR-\d{3}[a-z]?/).uniq
referenced_requirements = cases.flat_map { |test_case| test_case.fetch("requirement") }.uniq

abort "Duplicate acceptance case IDs" unless case_ids.uniq.length == case_ids.length
unknown_requirements = referenced_requirements - requirement_ids
abort "Unknown requirements: #{unknown_requirements.join(", ")}" unless unknown_requirements.empty?
abort "Acceptance case IDs must use AC-USx-yyy format" unless case_ids.all? { |id| id.match?(/\AAC-US\d+-\d{3}\z/) }

if command == "validate"
  puts "Validated #{cases.length} acceptance cases and #{referenced_requirements.length} requirements"
  exit
end

output_directory = ARGV.shift
usage unless output_directory

class_name = "GeneratedFunctionalTestCases"
package_name = "com.conduit.generated"
package_directory = File.join(output_directory, *package_name.split("."))
FileUtils.mkdir_p(package_directory)
output_path = File.join(package_directory, "#{class_name}.java")

methods = cases.map do |test_case|
  case_id = test_case.fetch("id")
  requirements = test_case.fetch("requirement")
  method_name = case_id.downcase.tr("-", "_")
  description = test_case.fetch("then").gsub('"', '\\"')
    tags = ([case_id] + requirements).map { |tag| "@Tag(\"#{tag}\")" }.join(", ")
  <<~JAVA
      @Tags({#{tags}})
      @Test
      void #{method_name}() {
          fail("Implement #{case_id}: #{description}");
      }
  JAVA
end.join("\n")

File.write(output_path, <<~JAVA)
  package #{package_name};

  import org.junit.jupiter.api.Tag;
  import org.junit.jupiter.api.Tags;
  import org.junit.jupiter.api.Test;

  import static org.junit.jupiter.api.Assertions.fail;

  /**
   * Generated from #{manifest_path}.
   * These tests are intentionally red until the feature implementation supplies the behavior.
   */
  class #{class_name} {
  #{methods}
  }
JAVA

puts "Generated #{cases.length} JUnit skeletons at #{output_path}"