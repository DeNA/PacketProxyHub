require_relative 'lib/snapshot/version'

Gem::Specification.new do |spec|
  spec.name          = "snapshot"
  spec.version       = Snapshot::VERSION
  spec.authors       = ["funa-tk"]
  spec.email         = ["1781263+funa-tk@users.noreply.github.com"]

  spec.summary       = %q{Create/Extract PPHS file.}
  spec.description   = %q{Create/Extract PPHS file.}
  spec.homepage      = "https://github.com/DeNA/PacketProxyHub"
  spec.license       = "Apache License 2"
  spec.required_ruby_version = Gem::Requirement.new(">= 2.3.0")

  spec.metadata["allowed_push_host"] = spec.homepage

  spec.metadata["homepage_uri"] = spec.homepage
  spec.metadata["source_code_uri"] = spec.homepage
  spec.metadata["changelog_uri"] = spec.homepage

  # Specify which files should be added to the gem when it is released.
  # The `git ls-files -z` loads the files in the RubyGem that have been added into git.
  spec.files         = Dir.chdir(File.expand_path('..', __FILE__)) do
    `git ls-files -z`.split("\x0").reject { |f| f.match(%r{^(test|spec|features)/}) }
  end
  spec.bindir        = "exe"
  spec.executables   = "snapshot"
  spec.require_paths = ["lib"]
  spec.add_dependency "google-protobuf", "3.19.2"
end
