require 'rspec'
require "snapshot/ini"

RSpec.describe 'ini' do

SAMPLE_INI = <<"SAMPLE"
avd.ini.encoding=UTF-8
path=/Users/foo.bar/.android/avd/Pixel_3a_API_31.avd
path.rel=avd/Pixel_3a_API_31.avd
target=android-31
SAMPLE

  before 'before' do
    @tmpdir = Dir.mktmpdir
    @tmpdir_path = Pathname(@tmpdir)
    @sample_ini_name = "sample.ini"
    @sample_ini_path = @tmpdir_path + @sample_ini_name
    File.write(@sample_ini_path, SAMPLE_INI)
  end
  after 'after' do
  end
  context 'basic tests' do
    it 'change user_name' do
      ini = Ini.new(@sample_ini_path)
      ini.change!("/Users/yamada.taro")
      puts File.read(@sample_ini_path)
    end
    it 'change avd_name' do
      ini = Ini.new(@sample_ini_path)
      ini.change_avd_name!("test")
      puts File.read(@sample_ini_path)
    end
  end
end
