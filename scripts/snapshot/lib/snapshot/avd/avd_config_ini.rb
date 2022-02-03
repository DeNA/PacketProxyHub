class AvdConfigIni

  attr_reader :google_play, :android_version

  def initialize(ini_path)
    @ini_path = ini_path
    @file_contents = File.read(@ini_path)
    @google_play = find_google_play(@file_contents)
    @android_version = find_android_version(@file_contents)
  end

  def change!(homedir_path)
    @file_contents = @file_contents.gsub(/\/Users\/[^\/]+/, homedir_path)
    File.write(@ini_path, @file_contents)
  end

  def change_avd_name!(avd_name)
    @file_contents = @file_contents.gsub(/[^\/]+\.avd/, avd_name + ".avd")
    @file_contents = @file_contents.gsub(/AvdId=.+/, "AvdId=" + avd_name)
    @file_contents = @file_contents.gsub(/avd\.ini\.displayname=.+/, "avd.ini.displayname=" + avd_name)
    File.write(@ini_path, @file_contents)
  end

  private

  def find_google_play(file_contents)
    google_play = false
    file_contents.each_line do |line|
      if line.start_with?("tag.display")
        google_play = true if line =~ /Google Play/
      end
    end
    google_play
  end

  def find_android_version(file_contents)
    android_version = "unknown"
    file_contents.each_line do |line|
      if line.start_with?("image.sysdir.1")
        android_version = line.match(/\/(android-[^\/]+)\//)[1]
      end
    end
    android_version
  end

end
