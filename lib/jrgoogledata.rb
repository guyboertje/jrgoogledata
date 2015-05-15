unless RUBY_PLATFORM =~ /java/
  puts "This library is only compatible with a java-based ruby environment like JRuby."
  exit 255
end

# require_relative "jars/jrgoogledata-1.0.2.jar"
require_relative "linked/jrgoogledata-1.0.3.jar"

require 'com/jrgoogledata/jrgoogledata'
require "jrgoogledata/version"
