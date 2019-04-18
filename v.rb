require 'pathname'

ten_percent = ((rand * 1_000_000) % 100 <= 15)

cats = Dir['cats/*']
cati = (rand * 1_000_000) % cats.size
cat = Pathname.new(cats[cati])

vals = cat.read.each_line.map(&:chomp).to_a
vali = (rand * 1_000_000) % vals.size
val = vals[vali]

if ten_percent
  puts "#{cat.basename}? fuck you!"
else
  puts "#{cat.basename}: #{val}."
end
