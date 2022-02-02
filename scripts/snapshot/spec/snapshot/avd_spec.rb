require 'rspec'
require "snapshot/avd/avd"

RSpec.describe 'Avd' do
  context 'test Avd' do
    it 'list avds' do
      puts Avd.list_avd
    end
    it 'create Avd' do
      puts Avd.new('orig')
    end
    it 'find snapshot' do
      avd = Avd.new('orig')
      puts avd.find_snapshot_by_name('hoge')
    end
    it 'create snapshot' do
      avd = Avd.new('orig')
      avd.create_binary("aaa", 'new')
    end
    it 'extract snapshot' do
      Avd.delete('new')
      Avd.extract_binary("new.snapshot")
    end
  end
end
