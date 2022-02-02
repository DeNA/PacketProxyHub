require 'pathname'
require 'tmpdir'
require 'google/protobuf'
require 'snapshot/ini'
require 'snapshot/avd/avd_config_ini'
require 'snapshot/avd/avd_emu_launch_params'
require 'snapshot/avd/avd_hardware_qemu_ini'
require 'snapshot/avd/snapshot/snap'

class Avd

  attr_reader :name, :dir_name, :dir_path, :ini_name, :ini_path, :snapshot_root_path, :snaps
  AVD_HOME_ROOT_PATH = Pathname(Dir.home) + '.android' + 'avd'

  def self.list_avd
    avds = []
    avd_root = Pathname(Dir.home) + '.android' + 'avd'
    Dir.glob((avd_root + '*.avd').to_s).each do |avd|
      avd_dirname = File.dirname(avd)
      avd_basename = File.basename(avd, '.avd')
      avd_ininame =  avd_basename + '.ini'
      avd_inipath = Pathname(avd_dirname) + avd_ininame
      if File.exists? avd_inipath
        avds << avd_basename
      end
    end
    return avds
  end

  def self.delete(avd_name, avd_root_path = AVD_HOME_ROOT_PATH)
    avd_root_path = Pathname(avd_root_path)
    FileUtils.rm_rf(avd_root_path + "#{avd_name}.avd")
    FileUtils.rm_rf(avd_root_path + "#{avd_name}.ini")
  end

  def initialize(avd_name, avd_root_path = AVD_HOME_ROOT_PATH)
    @AVD_ROOT_PATH = Pathname(avd_root_path)
    @name = avd_name
    @dir_name = avd_name + '.avd'
    @dir_path = @AVD_ROOT_PATH + @dir_name
    @ini_name = avd_name + '.ini'
    @ini_path = @AVD_ROOT_PATH + @ini_name
    @ini = Ini.new(@ini_path)
    @config_ini_path = @dir_path + "config.ini"
    @config_ini = AvdConfigIni.new(@config_ini_path)
    @hardware_qemu_ini_path = @dir_path + "hardware-qemu.ini"
    @hardware_qemu_ini = AvdHardwareQemuIni.new(@hardware_qemu_ini_path)
    @emu_launch_params_path = @dir_path + "emu-launch-params.txt"
    @emu_launch_params = AvdEmuLaunchParams.new(@emu_launch_params_path)
    @snapshot_root_path = @dir_path + 'snapshots'
    @snaps = []
    Dir.glob((@snapshot_root_path + '*').to_s).each do |snapshot|
      @snaps.push(Snap.new(Pathname.new(snapshot)))
    end
  end

  def delete_snapshot_if(snapshot_name)
    @snaps = @snaps.filter_map do |snap|
      (snap.name != snapshot_name) ? snap : (Snap.delete(snap.path) && nil)
    end
  end

  def delete_snapshot_unless(snapshot_name)
    @snaps = @snaps.filter_map do |snap|
      (snap.name == snapshot_name) ? snap : (Snap.delete(snap.path) && nil)
    end
  end

  def find_snapshot_by_name(snapshot_name)
    @snaps.find {|snap| snap.name == snapshot_name }
  end

  def change!(homedir_path)
    @ini.change!(homedir_path)
    @config_ini.change!(homedir_path)
    @hardware_qemu_ini.change!(homedir_path)
    @emu_launch_params.change!(homedir_path)
    @snaps.each do |snap|
      snap.change!(homedir_path)
    end
  end

  def change_avd_name!(avd_name)
    @ini.change_avd_name!(avd_name)
    @config_ini.change_avd_name!(avd_name)
    @hardware_qemu_ini.change_avd_name!(avd_name)
    @emu_launch_params.change_avd_name!(avd_name)
    @snaps.each do |snap|
      snap.change_avd_name!(avd_name)
    end

    @new_dir_name = avd_name + '.avd'
    @new_dir_path = @AVD_ROOT_PATH + @new_dir_name
    @new_ini_name = avd_name + '.ini'
    @new_ini_path = @AVD_ROOT_PATH + @new_ini_name
    puts "mv #{@dir_path} #{@new_dir_path}"
    FileUtils.mv(@dir_path, @new_dir_path)
    FileUtils.mv(@ini_path, @new_ini_path)

    initialize(avd_name, @AVD_ROOT_PATH)
  end

  def dup(new_root_dir)
    FileUtils.cp_r(@dir_path, new_root_dir)
    FileUtils.cp(@ini_path, new_root_dir)
    Avd.new(@name, new_root_dir)
  end

  def create_binary(snapshot_name, new_avd_name)
    Dir.mktmpdir do |tmpdir|
      tmpdir_path = Pathname(tmpdir)
      avd2 = dup(tmpdir_path)
      avd2.delete_snapshot_unless(snapshot_name)
      avd2.change_avd_name!(new_avd_name)
      snap = avd2.find_snapshot_by_name(snapshot_name)

      # create tar.gz
      tar_name = "#{avd2.name}.tar.gz"
      tar_path = tmpdir_path + tar_name
      break unless system("cd #{tmpdir} && tar czvf #{tar_name} #{avd2.dir_name} #{avd2.ini_name}")

      # create magic
      magic_name = "magic"
      magic_path = tmpdir_path + magic_name
      File.open(magic_path, "wb") {|f| f.write(["PPHS"].pack('a*'))}

      # create screenshot_size
      screenshot_size_name = "screenshot_size"
      screenshot_size_path = tmpdir_path + screenshot_size_name
      File.open(screenshot_size_path, "wb") {|f| f.write([snap.screenshot.size].pack('I'))}

      # create screenshot
      screenshot_name = "screenshot.png"
      FileUtils.cp(snap.screenshot_path, tmpdir_path)

      # binary
      binary_name = "#{avd2.name}.snapshot"
      binary_path = tmpdir_path + binary_name
      system("cd #{tmpdir} && cat #{magic_name} #{screenshot_size_name} #{screenshot_name} #{tar_name} > #{binary_name}")
      FileUtils.mv(binary_path, Dir.pwd)
    end
  end

  def create_binary_saved(snapshot_name, output_filename)
    Dir.mktmpdir do |tmpdir|
      tmpdir_path = Pathname(tmpdir)
      avd2 = dup(tmpdir_path)
      avd2.delete_snapshot_unless(snapshot_name)
      snap = avd2.find_snapshot_by_name(snapshot_name)

      # create tar.gz
      tar_name = "#{@name}.tar.gz"
      tar_path = tmpdir_path + tar_name
      break unless system("cd #{tmpdir} && tar czvf #{tar_name} #{@dir_name} #{@ini_name}")

      # create magic
      magic_name = "magic"
      magic_path = tmpdir_path + magic_name
      File.open(magic_path, "wb") {|f| f.write(["PPHS"].pack('a*'))}

      # create screenshot_size
      screenshot_size_name = "screenshot_size"
      screenshot_size_path = tmpdir_path + screenshot_size_name
      File.open(screenshot_size_path, "wb") {|f| f.write([snap.screenshot.size].pack('I'))}

      # create screenshot
      screenshot_name = "screenshot.png"
      FileUtils.cp(snap.screenshot_path, tmpdir_path)

      # binary
      binary_name = "#{@name}.snapshot"
      binary_path = tmpdir_path + binary_name
      system("cd #{tmpdir} && cat #{magic_name} #{screenshot_size_name} #{screenshot_name} #{tar_name} > #{binary_name}")
      FileUtils.mv(binary_path, Dir.pwd)
    end
  end

  def self.extract_binary(binary_path)
    Dir.mktmpdir do |tmpdir|
      tmpdir_path = Pathname(tmpdir)
      tar_name = "avd.tar.gz"
      tar_path = tmpdir_path + tar_name
      binary_file = File.open(binary_path, "rb")

      # check magic word
      magic_word = binary_file.read(4).unpack('a4')[0]
      if magic_word != "PPHS"
        puts "Error: incorrect PacketProxyHub snapshot file: #{binary_path}"
        break
      end

      # skip screenshot.png, and then extract tar ball
      screenshot_size = binary_file.read(4).unpack('I')[0]
      break unless system("dd if='#{binary_path}' of=#{tar_path} bs=#{screenshot_size+8} skip=1")
      break unless system("cd #{tmpdir} && tar xzvf #{tar_name}")

      # check $HOME/.android/avd/AVD_NAME.avd exists
      avd_name = File.basename(Dir.glob((tmpdir_path + "*.avd").to_s)[0])
      if File.exists? AVD_HOME_ROOT_PATH + avd_name
        puts "Error: already exists: #{AVD_HOME_ROOT_PATH + avd_name}"
        break
      end

      # check $HOME/.android/avd/AVD_NAME.ini exists
      ini_name = File.basename(Dir.glob((tmpdir_path + "*.ini").to_s)[0])
      if File.exists? AVD_HOME_ROOT_PATH + ini_name
        puts "Error: already exists: #{AVD_HOME_ROOT_PATH + ini_name}"
        break
      end

      # extract avd into $HOME/.android/avd/
      FileUtils.mv(tmpdir_path + avd_name, AVD_HOME_ROOT_PATH)
      FileUtils.mv(tmpdir_path + ini_name, AVD_HOME_ROOT_PATH)

      # fixup homedir paths in the avd
      avd = Avd.new(File.basename(avd_name, '.avd'))
      avd.change!(Dir.home)
    end
  end

end
