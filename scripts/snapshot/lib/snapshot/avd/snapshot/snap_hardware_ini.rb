class SnapHardwareIni

  def initialize(ini_path)
    @ini_path = ini_path
  end

  def change!(homedir_path)
    file_contents = File.read(@ini_path)
    file_contents = file_contents.gsub(/\/Users\/[^\/]+/, homedir_path)
    File.write(@ini_path, file_contents)
  end

  def change_avd_name!(avd_name)
    file_contents = ""
    File.open(@ini_path).each_line do |line|
      if line.start_with?("android.avd.home")
        file_contents << line
        next
      end
      if line.start_with?("avd.name =")
        file_contents << "avd.name = #{avd_name}\n"
        next
      end
      if line.start_with?("avd.id =")
        file_contents << "avd.id = #{avd_name}\n"
        next
      end
      file_contents << line.gsub(/[^\/]+\.avd/, avd_name + ".avd")
    end
    File.write(@ini_path, file_contents)
  end

end