require "snapshot/version"
require "snapshot/avd/snapshot/snap_meta_pb"

class SnapMeta

  attr_reader :name, :description

  def initialize(pbfile_path)
    @pbfile_path = pbfile_path
    File.open(pbfile_path) do |pbfile|
      @dirname = File.basename(pbfile_path.parent)
      @snapshot_ro = EmulatorSnapshot::Snapshot.decode(pbfile.read)
      @snapshot = @snapshot_ro.dup
      @name = @snapshot['logical_name'].empty? ? @dirname : @snapshot['logical_name']
      @description = @snapshot['description']
    end
  end

  def change!(homedir_path)
    @snapshot['images'].each do |image|
      old = image['path']
      image['path'] = old.gsub(/\/Users\/[^\/]+/, homedir_path)
    end
    @snapshot['launch_parameters'].map! do |param|
      param.gsub(/\/Users\/[^\/]+/, homedir_path)
    end
    pbfile = EmulatorSnapshot::Snapshot.encode(@snapshot)
    File.open(@pbfile_path, "wb") {|f| f.write(pbfile)}
  end

  def change_avd_name!(avd_name)
    @snapshot['images'].each do |image|
      old = image['path']
      image['path'] = old.gsub(/[^\/]+\.avd/, avd_name + ".avd")
    end
    avd_index = @snapshot['launch_parameters'].index('-avd')
    @snapshot['launch_parameters'][avd_index + 1] = avd_name
    pbfile = EmulatorSnapshot::Snapshot.encode(@snapshot)
    File.open(@pbfile_path, "wb") {|f| f.write(pbfile)}
  end

  def to_s
    "#{@name}:\t#{@description}"
  end

end
