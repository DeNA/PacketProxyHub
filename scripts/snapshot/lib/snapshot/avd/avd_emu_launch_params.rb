class AvdEmuLaunchParams

  def initialize(ini_path)
    @ini_path = ini_path
    @file_contents = File.read(@ini_path)
  end

  def change!(homedir_path)
    @file_contents = @file_contents.gsub(/\/Users\/[^\/]+/, homedir_path)
    File.write(@ini_path, @file_contents)
  end

  def change_avd_name!(avd_name)
    @file_contents = @file_contents.gsub(/[^\/]+\.avd/, avd_name + ".avd")
    File.write(@ini_path, @file_contents)
  end

end