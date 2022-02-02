require 'snapshot/avd/snapshot/snap_meta'
require 'snapshot/avd/snapshot/snap_hardware_ini'

class Snap

  attr_reader :name, :path, :screenshot_path, :screenshot, :meta

  def self.delete(path)
    FileUtils.rm_rf(path)
  end

  def initialize(snapshot_path)
    @path = snapshot_path
    @screenshot_path = snapshot_path + 'screenshot.png'
    @screenshot = File.open(@screenshot_path)
    @meta = SnapMeta.new(snapshot_path + 'snapshot.pb')
    @name = @meta.name
    @hardware_ini = SnapHardwareIni.new(snapshot_path + 'hardware.ini')
  end

  def change!(homedir_path)
    @hardware_ini.change!(homedir_path)
    @meta.change!(homedir_path)
  end

  def change_avd_name!(avd_name)
    @hardware_ini.change_avd_name!(avd_name)
    @meta.change_avd_name!(avd_name)
  end

end
