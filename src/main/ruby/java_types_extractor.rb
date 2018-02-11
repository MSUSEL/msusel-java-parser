#!/usr/bin/env ruby

require 'uri'
require 'nokogiri'
require 'open-uri'
require 'mechanize'

class JavaTypesExtractor
end

def process_package(name, url)
  file = File.open("data/#{name}.types", "w")

  begin
    file.puts "package #{name}"
    file.puts ""

    agent = Mechanize.new { |agent| agent.user_agent_alias = 'Mac Safari' }

    page = agent.get("#{$base_url}#{url}")
    page.search("//table[@class='typeSummary']//td[@class='colFirst']/a").each do |node|
      type = node['title'].split(" ")[0]
      name = node.text.split("<")[0]
      file.puts "#{type} #{name}"
    end
  rescue StandardError => error
    puts "Error: #{error}"
  ensure
    file.close
  end
end

$base_url = "https://docs.oracle.com/javase/8/docs/api/"

def main()
  retries = 0
  begin
    agent = Mechanize.new { |agent| agent.user_agent_alias = 'Mac Safari' }

    agent.agent.http.verify_mode = OpenSSL::SSL::VERIFY_NONE

    puts "Opening Page: #{$base_url}overview-summary.html"
    page = agent.get("#{$base_url}overview-summary.html")

    packages = {}

    page.search("//td[@class='colFirst']/a").each do |node|
      key = node.text
      val = node['href']
      puts "found package: #{key}@#{val}"
      packages[key] = val
    end

    packages.each do |key, val|
      process_package key, val
    end

  rescue StandardError => error
    puts "Error: #{error}"
    if (retries < 5)
      Kernel.sleep(1)
      retries += 1
      retry
    end
  end
end

if __FILE__ == $0
  main()
end